package acceptance.pk.lucidxpo.ynami.selenium.config;

import acceptance.pk.lucidxpo.ynami.common.config.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@TestConfiguration
public class SeleniumTestCaseContext implements BeanFactoryPostProcessor {

    private static final String TEST_SCOPE_NAME = "test";

    @Bean
    public TestScope testScope() {
        return new TestScope();
    }

    /*
     * Creating a Selenium web driver bean which will be injected into page objects.
     * Web drivers can’t be reused between tests so will create a custom scope for the driver bean called “test”.
     * This scope will create a new bean before each test.
     */
    @Bean
    @Scope(TEST_SCOPE_NAME)
    public WebDriver webDriver() {
        return WebDriverFactory.getDriver();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope(TEST_SCOPE_NAME, testScope());
    }
}