package pk.lucidxpo.ynami.spring.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import javax.validation.constraints.NotNull;

@Component
public class FeatureManagerWrapper {
    @Autowired
    private FeatureManager featureManager;

    public boolean isActive(@NotNull final Feature feature) {
        return featureManager.getFeatureState(feature).isEnabled();
    }

    public void activate(@NotNull final Feature feature) {
        featureManager.setFeatureState(new FeatureState(feature).enable());
    }

    public void deactivate(@NotNull final Feature feature) {
        featureManager.setFeatureState(new FeatureState(feature).disable());
    }
}