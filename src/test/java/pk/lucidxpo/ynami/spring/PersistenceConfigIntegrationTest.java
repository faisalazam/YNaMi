package pk.lucidxpo.ynami.spring;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.spring.security.SpringSecurityAuditAwareImpl;

import java.lang.reflect.InvocationHandler;

import static java.lang.reflect.Proxy.getInvocationHandler;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.spring.PersistenceConfig.AUDITOR_AWARE_BEAN_NAME;

@TestPropertySource(properties = "config.togglz.enabled=true")
class PersistenceConfigIntegrationTest extends AbstractIntegrationTest {
    @Test
    void shouldVerifyThatAuditorAwareBeanIsAnInstanceOfSpringSecurityAuditAwareImplWhenWebSecurityIsEnabled() throws Exception {
        final InvocationHandler invocationHandler = getInvocationHandler(applicationContext.getBean(AUDITOR_AWARE_BEAN_NAME));
        final Object auditorAwareBean = invocationHandler.getClass().getDeclaredMethod("getActive").invoke(invocationHandler);
        assertThat(auditorAwareBean, instanceOf(SpringSecurityAuditAwareImpl.class));
    }

    @Test
    void shouldVerifyThatAuditorAwareBeanIsAnInstanceOfAuditorAwareImplWhenWebSecurityIsDisabled() throws Exception {
        final InvocationHandler invocationHandler = getInvocationHandler(applicationContext.getBean(AUDITOR_AWARE_BEAN_NAME));
        final Object auditorAwareBean = invocationHandler.getClass().getDeclaredMethod("getInactive").invoke(invocationHandler);
        assertThat(auditorAwareBean, instanceOf(AuditorAwareImpl.class));
    }
}