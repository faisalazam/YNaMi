package acceptance.pk.lucidxpo.ynami.config.cucumber;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static acceptance.pk.lucidxpo.ynami.config.common.WebDriverFactory.getDriver;

@TestConfiguration
public class CucumberTestCaseContext {
    private static final String CUCUMBER_GLUE_SCOPE = "cucumber-glue";

    @Scope(CUCUMBER_GLUE_SCOPE)
    @Bean(destroyMethod = "quit")
    public WebDriver webDriver() {
        return getDriver();
    }
}