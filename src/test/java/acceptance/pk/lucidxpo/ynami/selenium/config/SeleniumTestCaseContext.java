package acceptance.pk.lucidxpo.ynami.selenium.config;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static acceptance.pk.lucidxpo.ynami.common.config.WebDriverFactory.getDriver;

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
        return getDriver();
    }
}