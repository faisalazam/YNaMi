package it.pk.lucidxpo.ynami.spring.security;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import it.pk.lucidxpo.ynami.spring.security.helper.DynamicTestsGenerator;
import it.pk.lucidxpo.ynami.spring.security.helper.EndPointMappingsLister;
import it.pk.lucidxpo.ynami.spring.security.helper.RequestMappingCustomizer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pk.lucidxpo.ynami.persistence.model.sample.Sample;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_DISABLED_AUTHENTICATED_WITHOUT_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_DISABLED_AUTHENTICATED_WITH_INVALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_DISABLED_AUTHENTICATED_WITH_VALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_DISABLED_UNAUTHENTICATED_WITHOUT_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_DISABLED_UNAUTHENTICATED_WITH_INVALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_DISABLED_UNAUTHENTICATED_WITH_VALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_ENABLED_AUTHENTICATED_WITHOUT_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_ENABLED_AUTHENTICATED_WITH_INVALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_ENABLED_AUTHENTICATED_WITH_VALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_ENABLED_UNAUTHENTICATED_WITHOUT_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_ENABLED_UNAUTHENTICATED_WITH_INVALID_CSRF;
import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenario.SECURITY_ENABLED_UNAUTHENTICATED_WITH_VALID_CSRF;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.context.TestSecurityContextHolder.clearContext;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class ApplicationEndpointsSecurityIntegrationTest extends AbstractIntegrationTest implements InitializingBean {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private static DynamicTestsGenerator DYNAMIC_TESTS_GENERATOR;

    private static Collection<Pair<String, RequestMappingCustomizer>> END_POINT_MAPPINGS_STREAM;

    // TODO: get the following endpoints working...
    private static final List<String> EXCLUDED_REQUEST_MAPPINGS_LIST = newArrayList(
            "[GET] /error",
            "[GET] /feature-test"
    );

    /**
     * A map, where key is the request mapping -> url/endpoint with HTTP method type
     * (i.e. something like "[GET] /actuator/togglz/{name}", and the value is the customized endpoint mapping
     */
    private static final Map<String, RequestMappingCustomizer> CUSTOMIZED_REQUEST_MAPPINGS_MAP = newHashMap();

    private static final EndPointMappingsLister END_POINT_MAPPINGS_LISTER = new EndPointMappingsLister(
            EXCLUDED_REQUEST_MAPPINGS_LIST, CUSTOMIZED_REQUEST_MAPPINGS_MAP
    );

    /**
     * Populating the 'CUSTOMIZED_REQUEST_MAPPINGS_MAP' map with the end points which requires customizations.
     * They are configured to let the testing mechanism know how to behave when any of these endpoints are encountered.
     * <p>
     * And finally instantiates the {@link DynamicTestsGenerator}
     */
    @Override
    public void afterPropertiesSet() {
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[GET] /samples/{id}",
                new RequestMappingCustomizer("GET",
                        "/samples/{id}",
                        Sample.class,
                        status().is3xxRedirection(),
                        view().name("sample/editSample")
                )
        );
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[GET] /samples/{id}/view",
                new RequestMappingCustomizer("GET",
                        "/samples/{id}/view",
                        Sample.class,
                        status().is3xxRedirection(),
                        view().name("sample/viewSample")
                )
        );
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[POST] /samples",
                new RequestMappingCustomizer("POST",
                        "/samples",
                        status().is3xxRedirection(),
                        redirectedUrl("/samples")
                )
        );
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[PUT] /samples/{id}",
                new RequestMappingCustomizer("PUT",
                        "/samples/{id}",
                        Sample.class,
                        status().is3xxRedirection(),
                        redirectedUrl("/samples")
                )
        );

        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[PATCH] /samples/{id}",
                new RequestMappingCustomizer("PATCH", "/samples/{id}", Sample.class)
        );
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[DELETE] /samples/{id}",
                new RequestMappingCustomizer("DELETE",
                        "/samples/{id}",
                        Sample.class,
                        status().is3xxRedirection(),
                        redirectedUrl("/samples")
                )
        );

        END_POINT_MAPPINGS_STREAM = END_POINT_MAPPINGS_LISTER.endPointMappingsCollection(requestMappingHandlerMapping);

        DYNAMIC_TESTS_GENERATOR = new DynamicTestsGenerator(applicationContext, featureManager, END_POINT_MAPPINGS_STREAM);
//  appEndpointHandlerMapping.getHandlerMethods().keySet().stream().map(t -> (t.getMethodsCondition().getMethods().size() == 0 ? "GET" : t.getMethodsCondition().getMethods().toArray()[0]) + " " + t.getPatternsCondition().getPatterns().toArray()[0]).toArray()
    }

    @AfterEach
    void close() {
        clearContext();
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsDisabledWithAuthenticatedAndValidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsDisabled(mockMvc, SECURITY_DISABLED_AUTHENTICATED_WITH_VALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsDisabledWithAuthenticatedAndInvalidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsDisabled(mockMvc, SECURITY_DISABLED_AUTHENTICATED_WITH_INVALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsDisabledWithAuthenticatedAndWithoutCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsDisabled(mockMvc, SECURITY_DISABLED_AUTHENTICATED_WITHOUT_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsDisabledWithUnauthenticatedAndValidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsDisabled(mockMvc, SECURITY_DISABLED_UNAUTHENTICATED_WITH_VALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsDisabledWithUnauthenticatedAndInvalidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsDisabled(mockMvc, SECURITY_DISABLED_UNAUTHENTICATED_WITH_INVALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsDisabledWithUnauthenticatedAndWithoutCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsDisabled(mockMvc, SECURITY_DISABLED_UNAUTHENTICATED_WITHOUT_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsEnabledWithAuthenticatedAndValidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsEnabled(mockMvc, SECURITY_ENABLED_AUTHENTICATED_WITH_VALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsEnabledWithAuthenticatedAndInvalidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsEnabled(mockMvc, SECURITY_ENABLED_AUTHENTICATED_WITH_INVALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllRequestMappingsAreFunctionalWhenWebSecurityIsEnabledWithAuthenticatedAndWithoutCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsEnabled(mockMvc, SECURITY_ENABLED_AUTHENTICATED_WITHOUT_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllSecuredRequestMappingsGetRedirectedToLoginPageWhenWebSecurityIsEnabledWithUnauthenticatedAndValidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsEnabled(mockMvc, SECURITY_ENABLED_UNAUTHENTICATED_WITH_VALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllSecuredRequestMappingsGetRedirectedToLoginPageWhenWebSecurityIsEnabledWithUnauthenticatedAndInvalidCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsEnabled(mockMvc, SECURITY_ENABLED_UNAUTHENTICATED_WITH_INVALID_CSRF);
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllSecuredRequestMappingsGetRedirectedToLoginPageWhenWebSecurityIsEnabledWithUnauthenticatedAndWithoutCsrf() {
        assertFalse(END_POINT_MAPPINGS_STREAM.isEmpty());

        return DYNAMIC_TESTS_GENERATOR.getDynamicTestsWhenWebSecurityIsEnabled(mockMvc, SECURITY_ENABLED_UNAUTHENTICATED_WITHOUT_CSRF);
    }
}