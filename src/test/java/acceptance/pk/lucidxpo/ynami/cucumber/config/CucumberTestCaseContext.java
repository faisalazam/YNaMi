package acceptance.pk.lucidxpo.ynami.cucumber.config;

import acceptance.pk.lucidxpo.ynami.common.config.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@TestConfiguration
public class CucumberTestCaseContext {
    private static final String CUCUMBER_GLUE_SCOPE = "cucumber-glue";

    @Scope(CUCUMBER_GLUE_SCOPE)
    @Bean(destroyMethod = "quit")
    public WebDriver webDriver() {
        return WebDriverFactory.getDriver();
    }
}