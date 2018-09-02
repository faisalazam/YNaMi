package pk.lucidxpo.ynami.spring.security;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.LogoutRequestBuilder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;
import static pk.lucidxpo.ynami.spring.security.SecurityConfig.LOGIN_PROCESSING_URL;
import static pk.lucidxpo.ynami.spring.security.SecurityConfig.LOGOUT_SUCCESS_URL;
import static pk.lucidxpo.ynami.spring.security.SecurityConfig.LOGOUT_URL;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class LoginLogoutSecurityConfigIntegrationTest extends AbstractIntegrationTest {

    @TestFactory
    Collection<DynamicTest> shouldVerifyLoginAttemptedWithCorrectCredentialAndHttpPostMethodAndValidCsrfIsSuccessful() {
        return getLoginRequestBuilderWithValidCsrf("admin", "admin").stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    mockMvc.perform(requestBuilder.getValue())
                            .andExpect(status().isFound())
                            .andExpect(redirectedUrl("/"))
                            .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"));
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyLoginAttemptedWithCorrectCredentialAndHttpPostMethodIsFailedWhenCsrfIsInvalid() {
        return getLoginRequestBuilderWithInvalidCsrf("admin", "admin").stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    performAndVerifyUnauthenticatedAndForbidden(requestBuilder.getValue());
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatLoginAttemptedWithIncorrectCredentialsIsFailedWhenCsrfIsValid() {
        return getLoginRequestBuilderWithValidCsrf(randomID(), randomID()).stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    final MvcResult mvcResult = mockMvc.perform(requestBuilder.getValue())
                            .andExpect(unauthenticated())
                            .andExpect(redirectedUrl("/login?error"))
                            .andReturn();
                    final HttpSession session = mvcResult.getRequest().getSession();
                    assertNotNull(session);

                    final Object securityLastException = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    assertThat(securityLastException, instanceOf(BadCredentialsException.class));
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatLoginAttemptedWithIncorrectCredentialsIsFailedWhenCsrfIsInvalid() {
        return getLoginRequestBuilderWithInvalidCsrf(randomID(), randomID()).stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    performAndVerifyUnauthenticatedAndForbidden(requestBuilder.getValue());
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatLoginAttemptedWithCorrectCredentialButWithIncorrectHttpMethodIsFailedRegardlessOfCsrfValidity() {
        return getMockHttpServletRequestBuilders(LOGIN_PROCESSING_URL).stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    mockMvc.perform(requestBuilder.getValue()
                            .param("username", "admin")
                            .param("password", "admin")
                    ).andExpect(unauthenticated());
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyLogoutAttemptedWithHttpPostMethodAndValidCsrfIsSuccessful() {
        return getLogoutRequestBuilderWithValidCsrf().stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    mockMvc.perform(requestBuilder.getValue())
                            .andExpect(redirectedUrl(LOGOUT_SUCCESS_URL))
                            .andExpect(status().is3xxRedirection())
                            .andExpect(unauthenticated());
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyLogoutAttemptedWithHttpPostMethodAndInvalidCsrfResultsInForbiddenError() {
        return getLogoutRequestBuilderWithInvalidCsrf().stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    performAndVerifyUnauthenticatedAndForbidden(requestBuilder.getValue());
                }))
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyLogoutAttemptedWithHttpGetMethodResultsInUnauthorizedErrorRegardlessOfCsrfValidity() {
        return getMockHttpServletRequestBuilders(LOGOUT_URL).stream()
                .map(requestBuilder -> dynamicTest(requestBuilder.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

                    mockMvc.perform(requestBuilder.getValue())
                            .andExpect(unauthenticated())
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrlPattern("**/login"));
                }))
                .collect(toList());
    }

    @Test
    @EnabledIf(value = "${togglz.features.WEB_SECURITY.enabled:false}", loadContext = true)
    void shouldVerifyAuthenticatedAndSecuredResourcesAreAccessibleWhenLogoutAttemptedAfterSuccessfulAuthenticationWithHttpPostMethodAndInvalidCsrf() throws Exception {
        final MvcResult authenticatedResult = mockMvc.perform(formLogin(LOGIN_PROCESSING_URL).user("admin").password("admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andReturn();
        final MockHttpSession authenticatedSession = (MockHttpSession) authenticatedResult.getRequest().getSession();
        assertNotNull(authenticatedSession);

        //Shouldn't allow logout when attempted with invalid csrf and should stay authenticated
        final MockHttpServletRequestBuilder requestBuilder = post(LOGOUT_URL);
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

    private List<Pair<String, LogoutRequestBuilder>> getLogoutRequestBuilderWithValidCsrf() {
        final LogoutRequestBuilder defaultRequestBuilder = logout(LOGOUT_URL);

        final LogoutRequestBuilder headerRequestBuilder = logout(LOGOUT_URL);
        setField(headerRequestBuilder, "postProcessor", csrf().asHeader());

        return newArrayList(
                Pair.of("Valid Csrf Token", defaultRequestBuilder),
                Pair.of("Valid Csrf Token as Header", headerRequestBuilder)
        );
    }

    private List<Pair<String, LogoutRequestBuilder>> getLogoutRequestBuilderWithInvalidCsrf() {
        return of(
                Pair.of("Invalid Csrf Token", csrf().useInvalidToken()),
                Pair.of("Invalid Csrf Token as Header", csrf().asHeader().useInvalidToken())
        ).map(postProcessor -> {
            final LogoutRequestBuilder requestBuilder = logout(LOGOUT_URL);
            setField(requestBuilder, "postProcessor", postProcessor.getValue());
            return Pair.of(postProcessor.getKey(), requestBuilder);
        }).collect(toList());
    }

    private List<Pair<String, FormLoginRequestBuilder>> getLoginRequestBuilderWithValidCsrf(final String username, final String password) {
        final FormLoginRequestBuilder defaultRequestBuilder = formLogin(LOGIN_PROCESSING_URL).user(username).password(password);

        final FormLoginRequestBuilder headerRequestBuilder = formLogin(LOGIN_PROCESSING_URL).user(username).password(password);
        setField(headerRequestBuilder, "postProcessor", csrf().asHeader());

        return newArrayList(
                Pair.of("Valid Csrf Token", defaultRequestBuilder),
                Pair.of("Valid Csrf Token as Header", headerRequestBuilder)
        );
    }

    private List<Pair<String, FormLoginRequestBuilder>> getLoginRequestBuilderWithInvalidCsrf(final String username, final String password) {
        return of(
                Pair.of("Invalid Csrf Token", csrf().useInvalidToken()),
                Pair.of("Invalid Csrf Token as Header", csrf().asHeader().useInvalidToken())
        ).map(postProcessor -> {
            final FormLoginRequestBuilder requestBuilder = formLogin(LOGIN_PROCESSING_URL).user(username).password(password);
            setField(requestBuilder, "postProcessor", postProcessor.getValue());
            return Pair.of(postProcessor.getKey(), requestBuilder);
        }).collect(toList());
    }

    private List<Pair<String, MockHttpServletRequestBuilder>> getMockHttpServletRequestBuilders(final String urlTemplate) {
        return newArrayList(
                Pair.of("Default Csrf Token", get(urlTemplate)),
                Pair.of("Valid Csrf Token", get(urlTemplate).with(csrf())),
                Pair.of("Valid Csrf Token as Header", get(urlTemplate).with(csrf().asHeader())),
                Pair.of("Invalid Csrf Token", get(urlTemplate).with(csrf().useInvalidToken())),
                Pair.of("Invalid Csrf Token as Header", get(urlTemplate).with(csrf().asHeader().useInvalidToken()))
        );
    }
}