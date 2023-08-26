package it.pk.lucidxpo.ynami.spring.security.helper;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import java.util.Collection;

import static it.pk.lucidxpo.ynami.spring.security.helper.EndPointTestScenarioExecutor.performAndAssertExpectations;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

public class DynamicTestsGenerator {
    private final ApplicationContext applicationContext;
    private final FeatureManagerWrappable featureManager;
    private final Collection<Pair<String, RequestMappingCustomizer>> endPointMappingsStream;

    public DynamicTestsGenerator(final ApplicationContext applicationContext,
                                 final FeatureManagerWrappable featureManager,
                                 final Collection<Pair<String, RequestMappingCustomizer>> endPointMappingsStream) {
        this.featureManager = featureManager;
        this.applicationContext = applicationContext;
        this.endPointMappingsStream = endPointMappingsStream;
    }

    /**
     * @return Generates and returns a collection of dynamic tests by going through the stream of endpoint mappings.
     */
    public Collection<DynamicTest> getDynamicTestsWhenWebSecurityIsEnabled(final MockMvc mockMvc,
                                                                           final EndPointTestScenario endPointTestScenario) {
        return endPointMappingsStream.stream()
                .map(requestBuilderPair -> dynamicTest(requestBuilderPair.getKey(), () -> {
                    assumeTrue(featureManager.isActive(WEB_SECURITY),
                            requestBuilderPair.getKey() + " test is ignored as Web Security is disabled"
                    );

                    performAndAssertExpectations(
                            mockMvc,
                            applicationContext,
                            requestBuilderPair.getValue(),
                            endPointTestScenario
                    );
                }))
                .collect(toList());
    }

    /**
     * @return Generates and returns a collection of dynamic tests by going through the stream of endpoint mappings.
     */
    public Collection<DynamicTest> getDynamicTestsWhenWebSecurityIsDisabled(final MockMvc mockMvc,
                                                                            final EndPointTestScenario endPointTestScenario) {
        return endPointMappingsStream.stream()
                .map(requestBuilderPair -> dynamicTest(requestBuilderPair.getKey(), () -> {
                    assumeFalse(featureManager.isActive(WEB_SECURITY),
                            requestBuilderPair.getKey() + " test is ignored as Web Security is enabled"
                    );

                    performAndAssertExpectations(
                            mockMvc,
                            applicationContext,
                            requestBuilderPair.getValue(),
                            endPointTestScenario
                    );
                }))
                .collect(toList());
    }
}
