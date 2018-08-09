package pk.lucidxpo.ynami.spring.features;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.values;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@TestPropertySource(properties = {
        "config.togglz.enabled=false"
})
public class FeatureManagerWrapperTogglzDisabledIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private FeatureManagerWrappable featureManagerWrappable;

    @Test
    public void shouldVerifyThatUserProviderBeanDoesNotExistWhenTogglzIsDisabled() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertBeanDoesNotExist(FeatureManager.class);
    }

    @Test
    public void shouldVerifyActivationDeactivationOfFeatureUsingCustomFeatureManagerHasNoImpact() {
        final Feature feature = chooseOneOf(values());
        featureManagerWrappable.deactivate(feature);

        assertThat(featureManagerWrappable.isActive(feature), is(false));

        featureManagerWrappable.activate(feature);

        assertThat(featureManagerWrappable.isActive(feature), is(false));
    }
}