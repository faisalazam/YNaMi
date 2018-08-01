package pk.lucidxpo.ynami.spring.features;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TogglzConfigurationIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void shouldVerifyThatFeatureProviderBeanExists() {
        assertThat(applicationContext.containsBeanDefinition("featureProvider"), is(true));
    }
}