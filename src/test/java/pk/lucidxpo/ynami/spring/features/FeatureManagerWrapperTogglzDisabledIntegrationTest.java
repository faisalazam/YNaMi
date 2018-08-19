package pk.lucidxpo.ynami.spring.features;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.values;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@TestPropertySource(properties = {
        "config.togglz.enabled=false"
})
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class FeatureManagerWrapperTogglzDisabledIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    @Qualifier("featureManagerDefaultWrapper")
    private FeatureManagerWrappable featureManager;

    @Test
    void shouldVerifyThatUserProviderBeanDoesNotExistWhenTogglzIsDisabled() {
        assertBeanDoesNotExist(FeatureManager.class);
    }

    @Test
    void shouldVerifyActivationDeactivationOfFeatureUsingCustomFeatureManagerHasNoImpact() {
        final Feature feature = chooseOneOf(values());
        featureManager.deactivate(feature);

        assertThat(featureManager.isActive(feature), is(false));

        featureManager.activate(feature);

        assertThat(featureManager.isActive(feature), is(false));
    }
}