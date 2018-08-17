package pk.lucidxpo.ynami.spring.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pk.lucidxpo.ynami.service.sample.NewToggleableServiceImpl;
import pk.lucidxpo.ynami.service.sample.OldToggleableServiceImpl;
import pk.lucidxpo.ynami.service.sample.ToggleableService;
import pk.lucidxpo.ynami.spring.features.FeatureProxyFactoryBeanWrapper;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.TOGGLEABLE_SERVICE;

@Configuration
public class ToggleableServiceConfiguration {

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public ToggleableService oldToggleableService() {
        return new OldToggleableServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public ToggleableService newToggleableService() {
        return new NewToggleableServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public FeatureProxyFactoryBeanWrapper proxiedToggleableService() {
        final FeatureProxyFactoryBeanWrapper proxyFactoryBean = new FeatureProxyFactoryBeanWrapper();
        proxyFactoryBean.setFeature(TOGGLEABLE_SERVICE.name());
        proxyFactoryBean.setProxyType(ToggleableService.class);
        proxyFactoryBean.setActive(newToggleableService());
        proxyFactoryBean.setInactive(oldToggleableService());
        return proxyFactoryBean;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public ToggleableService someService(@Autowired FeatureProxyFactoryBeanWrapper proxiedToggleableService) throws Exception {
        return (ToggleableService) proxiedToggleableService.getObject();
    }

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "false", matchIfMissing = true)
    public ToggleableService oldService() {
        return new OldToggleableServiceImpl();
    }
}