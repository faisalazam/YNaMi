package acceptance.pk.lucidxpo.ynami.webdriver.scope;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * This class is a Spring configuration class which is responsible for instantiating the
 * {@link TestMethodScopeBeanFactoryPostProcessor} and configuring a bean of type {@link BeanFactoryPostProcessor}.
 * This is the one which will be used to handle the beans annotated with {@link TestMethodScope}, which in this case,
 * will be {@link WebDriver}.
 */
@TestConfiguration
public class TestMethodScopeConfig {
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new TestMethodScopeBeanFactoryPostProcessor();
    }
}
