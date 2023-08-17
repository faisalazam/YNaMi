package pk.lucidxpo.ynami.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pk.lucidxpo.ynami.spring.features.FeatureProxyFactoryBeanWrapper;
import pk.lucidxpo.ynami.spring.security.SpringSecurityAuditAwareImpl;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class PersistenceConfig {
    static final String AUDITOR_AWARE_BEAN_NAME = "auditorAware";

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public AuditorAware<String> insecureAuditorAware() {
        return new AuditorAwareImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public AuditorAware<String> springSecurityAuditorAware() {
        return new SpringSecurityAuditAwareImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public FeatureProxyFactoryBeanWrapper proxiedAuditorAware() {
        final FeatureProxyFactoryBeanWrapper proxyFactoryBean = new FeatureProxyFactoryBeanWrapper();
        proxyFactoryBean.setFeature(WEB_SECURITY);
        proxyFactoryBean.setProxyType(AuditorAware.class);
        proxyFactoryBean.setActive(springSecurityAuditorAware());
        proxyFactoryBean.setInactive(insecureAuditorAware());
        return proxyFactoryBean;
    }

    @Primary
    @Bean(AUDITOR_AWARE_BEAN_NAME)
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "true")
    public AuditorAware auditorAware(@Autowired @Qualifier("proxiedAuditorAware") final FeatureProxyFactoryBeanWrapper proxiedAuditorAware) throws Exception {
        return (AuditorAware) proxiedAuditorAware.getObject();
    }

    @Bean(AUDITOR_AWARE_BEAN_NAME)
    @ConditionalOnProperty(name = "config.togglz.enabled", havingValue = "false", matchIfMissing = true)
    public AuditorAware auditorAwareWhenTogglzIsDisabled() {
        return new AuditorAwareImpl();
    }
}