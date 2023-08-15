package pk.lucidxpo.ynami.spring.features;

import jakarta.validation.constraints.NotNull;
import org.togglz.core.Feature;

public interface FeatureManagerWrappable {
    boolean isActive(@NotNull Feature feature);

    void activate(@NotNull Feature feature);

    void deactivate(@NotNull Feature feature);
}