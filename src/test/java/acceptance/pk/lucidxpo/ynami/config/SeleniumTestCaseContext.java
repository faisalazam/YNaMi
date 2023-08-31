package acceptance.pk.lucidxpo.ynami.config;

import acceptance.pk.lucidxpo.ynami.config.scope.TestMethodScopeBean;
import acceptance.pk.lucidxpo.ynami.config.scope.TestMethodScope;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SeleniumTestCaseContext {
    @Bean
    public TestMethodScope testMethodScope() {
        return new TestMethodScope();
    }

    /**
     * Creating a Selenium web driver bean which will be injected into page objects.
     * Web drivers can’t be reused between tests so will create a custom scope for the driver bean called
     * “testMethodScope”. This scope will create a new bean before each test.
     */
    @Bean
    @TestMethodScopeBean
    public WebDriver webDriver() {
        return WebDriverFactory.getDriver();
    }
}