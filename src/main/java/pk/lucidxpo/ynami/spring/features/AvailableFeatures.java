package pk.lucidxpo.ynami.spring.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;

public enum AvailableFeatures implements Feature {
    @Label("First Feature")
    FEATURE_ONE,

    @EnabledByDefault
    @Label("Second Feature")
    FEATURE_TWO;
}