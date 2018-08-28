package pk.lucidxpo.ynami.spring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class RedirectionSecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void before() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldVerifyThatAuthenticationIsRequiredWhenSecuredResourceIsAccessedUnauthenticated() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

        mockMvc.perform(get("/samples"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    void shouldVerifySuccessfulNavigationToSecuredResourceWhenCredentialsAreCorrect() throws Exception {
        mockMvc.perform(get("/samples").with(user("admin")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldVerifyRedirectedBackToSecuredResourceAfterAuthenticationWhenSecuredResourceIsAccessedUnauthenticated() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

        final MockHttpServletRequestBuilder securedResourceAccess = get("/samples");
        final MvcResult unauthenticatedResult = mockMvc.perform(securedResourceAccess)
                .andExpect(status().is3xxRedirection())
                .andReturn();

        final MockHttpSession session = (MockHttpSession) unauthenticatedResult.getRequest().getSession();
        final String loginUrl = unauthenticatedResult.getResponse().getRedirectedUrl();
        mockMvc.perform(post(loginUrl)
                .param("username", "admin")
                .param("password", "admin")
                .session(session)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/samples"))
                .andReturn();

        mockMvc.perform(securedResourceAccess.session(session))
                .andExpect(status().isOk());
    }
}