package acceptance.pk.lucidxpo.ynami.selenium.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * This class is a Spring configuration class which is responsible for loading our
 * {@link BeanFactoryPostProcessor} implementation (i.e. {@link TestMethodScopeBeanFactoryPostProcessor}).
 */
@TestConfiguration
public class TestMethodScopeConfig {
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new TestMethodScopeBeanFactoryPostProcessor();
    }
}
