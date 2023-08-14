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
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ActuatorEndpointsSecurityIntegrationTest extends AbstractIntegrationTest implements InitializingBean {

    @Autowired
    private WebMvcEndpointHandlerMapping actuatorEndpointHandlerMapping;

    private static DynamicTestsGenerator DYNAMIC_TESTS_GENERATOR;

    private static List<Pair<String, RequestMappingCustomizer>> END_POINT_MAPPINGS_STREAM;

    private static final Map<String, RequestMappingCustomizer> CUSTOMIZED_REQUEST_MAPPINGS_MAP = newHashMap();

    private static final EndPointMappingsLister END_POINT_MAPPINGS_LISTER = new EndPointMappingsLister(
            newArrayList("[GET] /actuator/heapdump"), CUSTOMIZED_REQUEST_MAPPINGS_MAP
    );

    @Override
    public void afterPropertiesSet() {
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[POST] /actuator/loggers/{name}", new RequestMappingCustomizer("POST", post("/actuator/loggers/{name}", "org.springframework.context").contentType("application/json"), status().isNoContent()));
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[GET] /actuator/metrics/{requiredMetricName}", new RequestMappingCustomizer("GET", get("/actuator/metrics/{requiredMetricName}", "system.cpu.count")));
        CUSTOMIZED_REQUEST_MAPPINGS_MAP.put("[GET] /actuator/env/{toMatch}", new RequestMappingCustomizer("GET", get("/actuator/env/{toMatch}", "togglz.enabled")));

        END_POINT_MAPPINGS_STREAM = END_POINT_MAPPINGS_LISTER.endPointMappingsCollection(actuatorEndpointHandlerMapping);

        DYNAMIC_TESTS_GENERATOR = new DynamicTestsGenerator(applicationContext, featureManager, END_POINT_MAPPINGS_STREAM);
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