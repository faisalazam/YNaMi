package pk.lucidxpo.ynami.spring.features;

import org.springframework.core.env.Environment;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureGroup;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.metadata.SimpleFeatureGroup;
import org.togglz.core.repository.FeatureState;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

class EnvironmentFeatureMetaData implements FeatureMetaData {
    private static final String TOGGLZ_FEATURES = "togglz.features.";

    private final Feature feature;
    private final Environment environment;

    EnvironmentFeatureMetaData(final Feature feature, final Environment environment) {
        this.feature = feature;
        this.environment = environment;
    }

    @Override
    public String getLabel() {
        return environment.getProperty(TOGGLZ_FEATURES + feature.name() + ".label", feature.name());
    }

    @Override
    public FeatureState getDefaultFeatureState() {
        final boolean defaultEnabledState = environment.getProperty(
                TOGGLZ_FEATURES + feature.name() + ".enabled", Boolean.class, false
        );
        return new FeatureState(feature, defaultEnabledState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<FeatureGroup> getGroups() {
        final Set<String> groups = environment.getProperty(TOGGLZ_FEATURES + feature.name() + ".groups", Set.class);
        return groups == null ? emptySet() : groups.stream().map(SimpleFeatureGroup::new).collect(toSet());
    }

    @Override
    public Map<String, String> getAttributes() {
        return emptyMap();
    }
}