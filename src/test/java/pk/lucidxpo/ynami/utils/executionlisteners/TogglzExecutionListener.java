package pk.lucidxpo.ynami.utils.executionlisteners;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;
import pk.lucidxpo.ynami.spring.features.FeatureToggles;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * There are integration tests which will be activating/deactivating the feature toggles in tests to test different
 * aspects of application. The changed state of feature toggles may impact the tests running after the ones which have
 * changed  the state of feature toggles. All that may happen when "config.togglz.enabled=true" in properties file.
 * <p>
 * In order to overcome this problem, we'll capture the state of the all the feature toggles before running the very
 * first test in a map, and then use that map later on at the end of every test class to reset the feature toggles
 * to their original/initial state.
 */
public class TogglzExecutionListener implements TestExecutionListener {
    /**
     * A map, where key is the name of the feature toggle and value is the state of the feature toggle before running
     * the very first test.
     */
    private static final Map<String, Boolean> FEATURE_ORIGINAL_STATE_MAP = new HashMap<>();

    private boolean togglzEnabled;
    private FeatureManagerWrappable featureManager;

    @Override
    public void beforeTestClass(final TestContext testContext) {
        final ApplicationContext applicationContext = testContext.getApplicationContext();
        togglzEnabled = parseBoolean(applicationContext.getEnvironment().getProperty("config.togglz.enabled"));

        if (!togglzEnabled) {
            return;
        }

        featureManager = applicationContext.getBean(FeatureManagerWrappable.class);
        // TODO: Ensure that this map is initialized with the correct feature states.
        stream(FeatureToggles.values())
                .forEach(
                        feature -> FEATURE_ORIGINAL_STATE_MAP.putIfAbsent(feature.name(), featureManager.isActive(feature))
                );
    }

    /**
     * Using the map at the end of every test class to reset the feature toggles to their original/initial state.
     */
    @Override
    public void afterTestClass(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        if (!togglzEnabled) {
            return;
        }

        for (FeatureToggles feature : FeatureToggles.values()) {
            final boolean wasActive = FEATURE_ORIGINAL_STATE_MAP.get(feature.name());
            if (wasActive) {
                featureManager.activate(feature);
                assertTrue(featureManager.isActive(feature));
            } else {
                featureManager.deactivate(feature);
                assertFalse(featureManager.isActive(feature));
            }
        }
    }
}