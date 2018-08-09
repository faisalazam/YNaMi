package pk.lucidxpo.ynami.spring.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import javax.validation.constraints.NotNull;

@Component
@ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
public class FeatureManagerWrapper implements FeatureManagerWrapable {
    @Autowired
    private FeatureManager featureManager;

    @Override
    public boolean isActive(@NotNull final Feature feature) {
        return featureManager.getFeatureState(feature).isEnabled();
    }

    @Override
    public void activate(@NotNull final Feature feature) {
        featureManager.setFeatureState(new FeatureState(feature).enable());
    }

    @Override
    public void deactivate(@NotNull final Feature feature) {
        featureManager.setFeatureState(new FeatureState(feature).disable());
    }
}