package acceptance.pk.lucidxpo.ynami.selenium.config;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static acceptance.pk.lucidxpo.ynami.common.config.WebDriverFactory.getDriver;
import static acceptance.pk.lucidxpo.ynami.selenium.config.TestMethodScopeBean.TEST_METHOD_SCOPE;

@TestConfiguration
public class SeleniumTestCaseContext implements BeanFactoryPostProcessor {
    /*
     * Creating a Selenium web driver bean which will be injected into page objects.
     * Web drivers can’t be reused between tests so will create a custom scope for the driver bean called “test”.
     * This scope will create a new bean before each test.
     */
    @Bean
    @TestMethodScopeBean
    public WebDriver webDriver() {
        return getDriver();
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope(TEST_METHOD_SCOPE, new TestMethodScope());
    }
}