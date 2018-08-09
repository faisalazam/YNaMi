package pk.lucidxpo.ynami.spring.features;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;

import javax.validation.constraints.NotNull;

@Component
@ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "false", matchIfMissing = true)
public class FeatureManagerDefaultWrapper implements FeatureManagerWrapable {

    @Override
    public boolean isActive(@NotNull final Feature feature) {
        return false;
    }

    @Override
    public void activate(@NotNull final Feature feature) {
    }

    @Override
    public void deactivate(@NotNull final Feature feature) {
    }
}