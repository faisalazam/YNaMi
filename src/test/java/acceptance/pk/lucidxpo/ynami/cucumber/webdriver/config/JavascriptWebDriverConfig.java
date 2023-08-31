package acceptance.pk.lucidxpo.ynami.cucumber.webdriver.config;

import acceptance.pk.lucidxpo.ynami.cucumber.annotations.LazyConfiguration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@LazyConfiguration
public class JavascriptWebDriverConfig {
    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public JavascriptExecutor javascriptExecutor(WebDriver driver) {
        return (JavascriptExecutor) driver;
    }
}