package acceptance.pk.lucidxpo.ynami.cucumber.webdriver.scope;

import acceptance.pk.lucidxpo.ynami.cucumber.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is responsible for instantiating the {@link WebdriverScopePostProcessor} and configuring a bean of type
 * {@link BeanFactoryPostProcessor}. This is the one which will be used to handle the beans annotated with
 * {@link WebdriverBeanScope}, which in this case, will be {@link WebDriver}.
 */
@Configuration
public class WebdriverScopeConfig {
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new WebdriverScopePostProcessor();
    }
}
