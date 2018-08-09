package pk.lucidxpo.ynami.spring.features;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;

import javax.validation.constraints.NotNull;

@Component
@Profile("!togglz")
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