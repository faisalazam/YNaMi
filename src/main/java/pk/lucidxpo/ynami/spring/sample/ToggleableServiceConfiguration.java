package pk.lucidxpo.ynami.spring.sample;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ToggleableService oldToggleableService() {
        return new OldToggleableServiceImpl();
    }

    @Bean
    public ToggleableService newToggleableService() {
        return new NewToggleableServiceImpl();
    }

    @Bean
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
    public ToggleableService someService(@Autowired FeatureProxyFactoryBeanWrapper proxiedToggleableService) throws Exception {
        return (ToggleableService) proxiedToggleableService.getObject();
    }
}