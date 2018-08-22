package pk.lucidxpo.ynami.spring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class LoginLogoutSecurityConfigIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void before() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldVerifySuccessfulLoginWithCorrectCredentialAndWithHttpPostMethod() throws Exception {
        mockMvc.perform(
                formLogin("/login")
                        .user("admin")
                        .password("admin")
        ).andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("admin"));
    }

    @Test
    void shouldVerifyThatLoginFailsWhenAttemptedWithIncorrectCredentials() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(
                formLogin("/login")
                        .user(randomID())
                        .password(randomID())
        ).andExpect(redirectedUrl("/login?error"))
                .andReturn();
        final Object securityLastException = mvcResult.getRequest().getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        assertThat(securityLastException, instanceOf(BadCredentialsException.class));
    }

    @Test
    void shouldVerifySuccessfulLogoutWithHttpPostMethod() throws Exception {
        mockMvc.perform(logout("/perform_logout"))
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldVerifyLogoutWithHttpGetMethodRedirectsToLoginPage() throws Exception {
        mockMvc.perform(get("/perform_logout").with(csrf()))
                .andExpect(redirectedUrlPattern("**/login"))
                .andExpect(status().is3xxRedirection());
    }
}