package pk.lucidxpo.ynami.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pk.lucidxpo.ynami.spring.security.SpringSecurityAuditAwareImpl;

import static pk.lucidxpo.ynami.spring.PersistenceConfig.AUDITOR_AWARE_BEAN_NAME;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = AUDITOR_AWARE_BEAN_NAME)
public class PersistenceConfig {
    static final String AUDITOR_AWARE_BEAN_NAME = "auditorAware";

    @Bean(AUDITOR_AWARE_BEAN_NAME)
    @ConditionalOnProperty(name = "config.web.security.enabled", havingValue = "true")
    public AuditorAware<String> springSecurityAuditorAware() {
        return new SpringSecurityAuditAwareImpl();
    }

    @Bean(AUDITOR_AWARE_BEAN_NAME)
    @ConditionalOnProperty(name = "config.web.security.enabled", havingValue = "false", matchIfMissing = true)
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}