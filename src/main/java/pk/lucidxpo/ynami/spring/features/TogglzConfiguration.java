package pk.lucidxpo.ynami.spring.features;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

@Configuration
public class TogglzConfiguration {
    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(AvailableFeatures.class);
    }
}