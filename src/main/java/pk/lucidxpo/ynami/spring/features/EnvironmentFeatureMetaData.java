package pk.lucidxpo.ynami.spring.features;

import org.springframework.core.env.Environment;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureGroup;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.metadata.SimpleFeatureGroup;
import org.togglz.core.repository.FeatureState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;

public class EnvironmentFeatureMetaData implements FeatureMetaData {
    private static final String TOGGLZ_FEATURES = "togglz.features.";

    private Feature feature;
    private Environment environment;

    public EnvironmentFeatureMetaData(final Feature feature, final Environment environment) {
        this.feature = feature;
        this.environment = environment;
    }

    @Override
    public String getLabel() {
        return environment.getProperty(TOGGLZ_FEATURES + feature.name() + ".label", feature.name());
    }

    @Override
    public FeatureState getDefaultFeatureState() {
        boolean defaultEnabledState = environment.getProperty(TOGGLZ_FEATURES + feature.name() + ".enabled", Boolean.class, false);
        return new FeatureState(feature, defaultEnabledState);
    }

    @Override
    public Set<FeatureGroup> getGroups() {
        String group = environment.getProperty(TOGGLZ_FEATURES + feature.name() + ".group");
        final HashSet<FeatureGroup> featureGroups = new HashSet<>();
        if (group != null) {
            featureGroups.add(new SimpleFeatureGroup(group));
        }
        return featureGroups;
    }

    @Override
    public Map<String, String> getAttributes() {
        return emptyMap();
    }
}