package pk.lucidxpo.ynami.spring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.LogoutRequestBuilder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;
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
    void shouldVerifyLoginAttemptedWithCorrectCredentialAndHttpPostMethodAndValidCsrfIsSuccessful() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final List<FormLoginRequestBuilder> requestBuilders = getLoginRequestBuilderWithValidCsrf("admin", "admin");
        for (final FormLoginRequestBuilder requestBuilder : requestBuilders) {
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"));
        }
    }

    @Test
    void shouldVerifyLoginAttemptedWithCorrectCredentialAndHttpPostMethodIsFailedWhenCsrfIsInvalid() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final List<FormLoginRequestBuilder> requestBuilders = getLoginRequestBuilderWithInvalidCsrf("admin", "admin");
        for (final FormLoginRequestBuilder requestBuilder : requestBuilders) {
            performAndVerifyUnauthenticatedAndForbidden(requestBuilder);
        }
    }

    @Test
    void shouldVerifyThatLoginAttemptedWithIncorrectCredentialsIsFailedRegardlessOfCsrfValidity() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final String username = randomID();
        final String password = randomID();
        for (final FormLoginRequestBuilder requestBuilder : getLoginRequestBuilderWithValidCsrf(username, password)) {
            final MvcResult mvcResult = mockMvc.perform(requestBuilder)
                    .andExpect(unauthenticated())
                    .andExpect(redirectedUrl("/login?error"))
                    .andReturn();
            final Object securityLastException = mvcResult.getRequest().getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            assertThat(securityLastException, instanceOf(BadCredentialsException.class));
        }

        for (final RequestBuilder requestBuilder : getLoginRequestBuilderWithInvalidCsrf(username, password)) {
            performAndVerifyUnauthenticatedAndForbidden(requestBuilder);
        }
    }

    @Test
    void shouldVerifyThatLoginAttemptedWithCorrectCredentialButWithIncorrectHttpMethodIsFailedRegardlessOfCsrfValidity() throws Exception {
        final List<MockHttpServletRequestBuilder> requestBuilders = getMockHttpServletRequestBuilders("/login");
        for (final MockHttpServletRequestBuilder requestBuilder : requestBuilders) {
            mockMvc.perform(requestBuilder
                    .param("username", "admin")
                    .param("password", "admin")
            ).andExpect(unauthenticated());
        }
    }

    @Test
    void shouldVerifyLogoutAttemptedWithHttpPostMethodAndValidCsrfIsSuccessful() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final List<LogoutRequestBuilder> requestBuilders = getLogoutRequestBuilderWithValidCsrf();
        for (final LogoutRequestBuilder requestBuilder : requestBuilders) {
            mockMvc.perform(requestBuilder)
                    .andExpect(redirectedUrl("/login?logout"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(unauthenticated());
        }
    }

    @Test
    void shouldVerifyLogoutAttemptedWithHttpPostMethodAndInvalidCsrfResultsInForbiddenError() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final List<LogoutRequestBuilder> requestBuilders = getLogoutRequestBuilderWithInvalidCsrf();
        for (final LogoutRequestBuilder requestBuilder : requestBuilders) {
            performAndVerifyUnauthenticatedAndForbidden(requestBuilder);
        }
    }

    @Test
    void shouldVerifyLogoutAttemptedWithHttpGetMethodRedirectsToLoginPageRegardlessOfCsrfValidity() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final List<MockHttpServletRequestBuilder> requestBuilders = getMockHttpServletRequestBuilders("/perform_logout");
        for (final MockHttpServletRequestBuilder requestBuilder : requestBuilders) {
            mockMvc.perform(requestBuilder)
                    .andExpect(redirectedUrlPattern("**/login"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(unauthenticated());
        }
    }

    @Test
    void shouldVerifyAuthenticatedAndSecuredResourcesAreAccessibleWhenLogoutAttemptedAfterSuccessfulAuthenticationWithHttpPostMethodAndInvalidCsrf() throws Exception {
        assumeTrue(featureManager.isActive(WEB_SECURITY));

        final MvcResult authenticatedResult = mockMvc.perform(formLogin().user("admin").password("admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andReturn();
        final MockHttpSession authenticatedSession = (MockHttpSession) authenticatedResult.getRequest().getSession();
        assertNotNull(authenticatedSession);

        //Shouldn't allow logout when attempted with invalid csrf and should stay authenticated
        final MockHttpServletRequestBuilder requestBuilder = post("/perform_logout");
        mockMvc.perform(requestBuilder.session(authenticatedSession).with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden())
                .andExpect(authenticated());

        final MockHttpServletRequestBuilder securedResourceAccess = get("/samples");
        mockMvc.perform(securedResourceAccess.session(authenticatedSession))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(status().isOk());

        //Should logout successfully when attempted with valid csrf and shouldn't stay authenticated
        mockMvc.perform(requestBuilder.session(authenticatedSession).with(csrf()))
                .andExpect(unauthenticated());

        mockMvc.perform(securedResourceAccess.session(authenticatedSession))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection());
    }

    private void performAndVerifyUnauthenticatedAndForbidden(final RequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(unauthenticated());
    }

    private List<LogoutRequestBuilder> getLogoutRequestBuilderWithValidCsrf() {
        final String url = "/perform_logout";
        final LogoutRequestBuilder defaultRequestBuilder = logout(url);

        final LogoutRequestBuilder headerRequestBuilder = logout(url);
        setField(headerRequestBuilder, "postProcessor", csrf().asHeader());

        return newArrayList(
                defaultRequestBuilder, headerRequestBuilder
        );
    }

    private List<LogoutRequestBuilder> getLogoutRequestBuilderWithInvalidCsrf() {
        final String url = "/perform_logout";
        return of(csrf().useInvalidToken(), csrf().asHeader().useInvalidToken())
                .map(postProcessor -> {
                    final LogoutRequestBuilder requestBuilder = logout(url);
                    setField(requestBuilder, "postProcessor", postProcessor);
                    return requestBuilder;
                })
                .collect(toList());


    }

    private List<FormLoginRequestBuilder> getLoginRequestBuilderWithValidCsrf(final String username, final String password) {
        final String url = "/login";
        final FormLoginRequestBuilder defaultRequestBuilder = formLogin(url).user(username).password(password);

        final FormLoginRequestBuilder headerRequestBuilder = formLogin(url).user(username).password(password);
        setField(headerRequestBuilder, "postProcessor", csrf().asHeader());

        return newArrayList(
                defaultRequestBuilder, headerRequestBuilder
        );
    }

    private List<FormLoginRequestBuilder> getLoginRequestBuilderWithInvalidCsrf(final String username, final String password) {
        final String url = "/login";
        return of(csrf().useInvalidToken(), csrf().asHeader().useInvalidToken())
                .map(postProcessor -> {
                    final FormLoginRequestBuilder requestBuilder = formLogin(url).user(username).password(password);
                    setField(requestBuilder, "postProcessor", postProcessor);
                    return requestBuilder;
                })
                .collect(toList());
    }

    private List<MockHttpServletRequestBuilder> getMockHttpServletRequestBuilders(final String urlTemplate) {
        return newArrayList(
                get(urlTemplate),
                get(urlTemplate).with(csrf()),
                get(urlTemplate).with(csrf().asHeader()),
                get(urlTemplate).with(csrf().useInvalidToken()),
                get(urlTemplate).with(csrf().asHeader().useInvalidToken())
        );
    }
}