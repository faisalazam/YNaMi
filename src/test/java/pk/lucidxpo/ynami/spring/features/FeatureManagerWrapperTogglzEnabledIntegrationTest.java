package pk.lucidxpo.ynami.spring.features;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.values;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@TestPropertySource(properties = {
        "config.togglz.enabled=true"
})
class FeatureManagerWrapperTogglzEnabledIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private FeatureManager featureManager;

    @Autowired
    @Qualifier("featureManagerWrapper")
    private FeatureManagerWrappable featureManagerWrappable;

    @Test
    void shouldVerifyActivationDeactivationOfFeatureUsingFeatureManager() {
        final Feature feature = chooseOneOf(values());
        featureManager.setFeatureState(new FeatureState(feature, false));

        assertThat(featureManager.isActive(feature), is(false));
        assertThat(featureManagerWrappable.isActive(feature), is(false));

        featureManager.setFeatureState(new FeatureState(feature, true));

        assertThat(featureManager.isActive(feature), is(true));

        assertThat(featureManagerWrappable.isActive(feature), is(true));
    }

    @Test
    void shouldVerifyActivationDeactivationOfFeatureUsingCustomFeatureManager() {
        final Feature feature = chooseOneOf(values());
        featureManagerWrappable.deactivate(feature);

        assertThat(featureManager.isActive(feature), is(false));
        assertThat(featureManagerWrappable.isActive(feature), is(false));

        featureManagerWrappable.activate(feature);

        assertThat(featureManager.isActive(feature), is(true));

        assertThat(featureManagerWrappable.isActive(feature), is(true));
    }
}