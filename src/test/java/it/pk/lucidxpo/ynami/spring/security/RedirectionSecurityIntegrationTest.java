package it.pk.lucidxpo.ynami.spring.security;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pk.lucidxpo.ynami.spring.security.SecurityConfig;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.eclipse.jetty.http.HttpHeaderValue.NO_CACHE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY;
import static org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN;
import static org.springframework.security.web.server.header.XContentTypeOptionsServerHttpHeadersWriter.NOSNIFF;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getField;

@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class RedirectionSecurityIntegrationTest extends AbstractIntegrationTest {
    @Test
    @EnabledIf(value = "${togglz.features.WEB_SECURITY.enabled:false}", loadContext = true)
    void shouldVerifyThatAuthenticationIsRequiredWhenSecuredResourceIsAccessedUnauthenticated() throws Exception {
        mockMvc.perform(get("/samples"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldVerifyDefaultHeaders() throws Exception {
        final ResultActions actions = mockMvc.perform(get("/"));
        assertHeadersExcludingXFrameOptions(actions)
                .andExpect(header().string("X-Frame-Options", is(DENY.name())));
    }

    @Test
    @DisabledIf(expression = "#{environment.acceptsProfiles('h2')}", loadContext = true)
    void shouldVerifyH2HeadersWhenH2ProfileIsInActive(
            @Value("${spring.h2.console.path:/h2-console}") final String h2ConsolePath) throws Exception {
        final ResultActions actions = mockMvc.perform(get(h2ConsolePath));
        assertHeadersExcludingXFrameOptions(actions)
                .andExpect(header().string("X-Frame-Options", is(DENY.name())));
    }

    @Test
    @EnabledIf(expression = "#{environment.acceptsProfiles('h2')}", loadContext = true)
    void shouldVerifyH2HeadersWhenH2ProfileIsActive(
            @Value("${spring.h2.console.path:/h2-console}") final String h2ConsolePath) throws Exception {
        final ResultActions actions = mockMvc.perform(get(h2ConsolePath));
        assertHeadersExcludingXFrameOptions(actions)
                .andExpect(header().string("X-Frame-Options", is(SAMEORIGIN.name())));
    }

    @Test
    void shouldVerifySuccessfulNavigationToSecuredResourceWhenCredentialsAreCorrect() throws Exception {
        mockMvc.perform(get("/samples").with(user("admin")))
                .andExpect(status().isOk());
    }

    @Test
    @EnabledIf(value = "${togglz.features.WEB_SECURITY.enabled:false}", loadContext = true)
    void shouldVerifyRedirectedBackToSecuredResourceAfterAuthenticationWhenSecuredResourceIsAccessedUnauthenticated() throws Exception {
        final MockHttpServletRequestBuilder securedResourceAccess = get("/samples");
        final MvcResult unauthenticatedResult = mockMvc.perform(securedResourceAccess)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
                .andExpect(unauthenticated())
                .andReturn();

        final MockHttpSession session = (MockHttpSession) unauthenticatedResult.getRequest().getSession();
        assertNotNull(session);

        final String loginProcessingUrl = (String) getField(SecurityConfig.class, null, "LOGIN_PROCESSING_URL");
        mockMvc.perform(post(loginProcessingUrl)
                        .param("username", "admin")
                        .param("password", "admin")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/samples?continue"))
                .andExpect(authenticated())
                .andReturn();

        mockMvc.perform(securedResourceAccess.session(session))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    private static ResultActions assertHeadersExcludingXFrameOptions(ResultActions actions) throws Exception {
        return actions
                .andExpect(header().string("X-Content-Type-Options", is(NOSNIFF)))
                .andExpect(header().string("X-XSS-Protection", notNullValue()))
                .andExpect(header().string("Cache-Control", notNullValue()))
                .andExpect(header().string("Pragma", is(NO_CACHE.asString())));
    }
}