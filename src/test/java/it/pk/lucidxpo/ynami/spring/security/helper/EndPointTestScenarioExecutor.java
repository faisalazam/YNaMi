package it.pk.lucidxpo.ynami.spring.security.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static it.pk.lucidxpo.ynami.spring.security.helper.AuthenticationSetter.setupAuthentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EndPointTestScenarioExecutor {

    static void performAndAssertExpectations(final MockMvc mockMvc,
                                             final ApplicationContext applicationContext,
                                             final RequestMappingCustomizer requestMappingCustomizer,
                                             final EndPointTestScenario endPointTestScenario) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = requestMappingCustomizer.getRequestBuilder(applicationContext);

        switch (endPointTestScenario) {
            case SECURITY_DISABLED_AUTHENTICATED_WITHOUT_CSRF:
                requestBuilder.with(user(setupAuthentication()));
                break;
            case SECURITY_DISABLED_AUTHENTICATED_WITH_VALID_CSRF,
                    SECURITY_ENABLED_AUTHENTICATED_WITH_VALID_CSRF:
                requestBuilder.with(user(setupAuthentication())).with(csrf());
                break;
            case SECURITY_DISABLED_AUTHENTICATED_WITH_INVALID_CSRF:
                requestBuilder.with(user(setupAuthentication())).with(csrf().useInvalidToken());
                break;
            case SECURITY_DISABLED_UNAUTHENTICATED_WITH_VALID_CSRF:
                requestBuilder.with(csrf());
                break;
            case SECURITY_DISABLED_UNAUTHENTICATED_WITH_INVALID_CSRF:
                requestBuilder.with(csrf().useInvalidToken());
                break;
            case SECURITY_DISABLED_UNAUTHENTICATED_WITHOUT_CSRF:
                break;

            case SECURITY_ENABLED_AUTHENTICATED_WITHOUT_CSRF:
                requestBuilder.with(user(setupAuthentication()));
                if ("GET".equalsIgnoreCase(requestMappingCustomizer.getMethodType())) {
                    final ResultActions resultActions = mockMvc.perform(requestBuilder);
                    requestMappingCustomizer.assertExpectations(resultActions);
                } else {
                    performAndAssertForbidden(mockMvc, requestBuilder);
                }
                return;
            case SECURITY_ENABLED_AUTHENTICATED_WITH_INVALID_CSRF:
                requestBuilder.with(user(setupAuthentication())).with(csrf().useInvalidToken());
                if ("GET".equalsIgnoreCase(requestMappingCustomizer.getMethodType())) {
                    final ResultActions resultActions = mockMvc.perform(requestBuilder);
                    requestMappingCustomizer.assertExpectations(resultActions);
                } else {
                    performAndAssertForbidden(mockMvc, requestBuilder);
                }
                return;
            case SECURITY_ENABLED_UNAUTHENTICATED_WITH_VALID_CSRF:
                requestBuilder.with(csrf());
                performAndAssertRedirectionToLogin(mockMvc, requestBuilder);
                return;
            case SECURITY_ENABLED_UNAUTHENTICATED_WITH_INVALID_CSRF:
                requestBuilder.with(csrf().useInvalidToken());
                performAndAssertExpectations(mockMvc, requestBuilder, requestMappingCustomizer);
                return;
            case SECURITY_ENABLED_UNAUTHENTICATED_WITHOUT_CSRF:
                performAndAssertExpectations(mockMvc, requestBuilder, requestMappingCustomizer);
                return;
            default:
                throw new IllegalArgumentException("Should never get into the default case");
        }

        final ResultActions resultActions = mockMvc.perform(requestBuilder);
        requestMappingCustomizer.assertExpectations(resultActions);
    }

    private static void performAndAssertExpectations(final MockMvc mockMvc,
                                                     final MockHttpServletRequestBuilder requestBuilder,
                                                     final RequestMappingCustomizer requestMappingCustomizer) throws Exception {
        if ("GET".equalsIgnoreCase(requestMappingCustomizer.getMethodType())) {
            performAndAssertRedirectionToLogin(mockMvc, requestBuilder);
        } else {
            performAndAssertForbidden(mockMvc, requestBuilder);
        }
    }

    private static void performAndAssertForbidden(final MockMvc mockMvc,
                                                  final MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    private static void performAndAssertRedirectionToLogin(final MockMvc mockMvc,
                                                           final MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
                .andExpect(unauthenticated());
    }
}