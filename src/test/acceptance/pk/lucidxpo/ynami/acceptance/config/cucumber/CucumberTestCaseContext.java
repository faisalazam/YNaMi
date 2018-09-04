package pk.lucidxpo.ynami.acceptance.config.cucumber;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static pk.lucidxpo.ynami.acceptance.config.common.WebDriverFactory.getDriver;


@TestConfiguration
public class CucumberTestCaseContext {
    @Bean(destroyMethod = "quit")
    public WebDriver webDriver() {
        return getDriver();
    }
}