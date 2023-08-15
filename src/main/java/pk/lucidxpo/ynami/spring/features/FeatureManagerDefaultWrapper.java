package pk.lucidxpo.ynami.spring.features;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;

@Component
@ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "false", matchIfMissing = true)
public class FeatureManagerDefaultWrapper implements FeatureManagerWrappable {

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