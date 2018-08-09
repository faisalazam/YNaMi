package pk.lucidxpo.ynami.spring.features;

import org.togglz.core.Feature;

import javax.validation.constraints.NotNull;

public interface FeatureManagerWrapable {
    boolean isActive(@NotNull Feature feature);

    void activate(@NotNull Feature feature);

    void deactivate(@NotNull Feature feature);
}