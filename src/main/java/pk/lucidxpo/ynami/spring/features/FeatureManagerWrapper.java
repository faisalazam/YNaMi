package pk.lucidxpo.ynami.spring.features;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

@Component
@ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
public class FeatureManagerWrapper implements FeatureManagerWrappable {
    private final FeatureManager featureManager;

    @Autowired
    public FeatureManagerWrapper(final FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

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