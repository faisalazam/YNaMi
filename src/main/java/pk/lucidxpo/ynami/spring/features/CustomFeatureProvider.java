package pk.lucidxpo.ynami.spring.features;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.spi.FeatureProvider;

import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.values;

@Component
@ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
class CustomFeatureProvider implements FeatureProvider {

    private final Environment environment;

    public CustomFeatureProvider(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Set<Feature> getFeatures() {
        return stream(values()).collect(toSet());
    }

    @Override
    public FeatureMetaData getMetaData(Feature feature) {
        return new EnvironmentFeatureMetaData(feature, environment);
    }
}