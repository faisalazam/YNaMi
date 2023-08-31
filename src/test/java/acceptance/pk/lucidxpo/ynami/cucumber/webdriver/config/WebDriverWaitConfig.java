package acceptance.pk.lucidxpo.ynami.cucumber.webdriver.config;

import acceptance.pk.lucidxpo.ynami.cucumber.annotations.LazyConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static java.time.Duration.ofMillis;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * WebDriverWait is applied on certain element with defined expected condition and time. This wait is only applied to
 * the specified element. This wait can also throw exception when element is not found.
 * <p>
 * e.g. webdriverWait.until(ExpectedConditions.titleIs("Software Testing Material - A site for Software Testers"));
 * <p>
 * An exception is thrown if the condition is not satisfied in specified timeout.
 * <p>
 * This class is responsible for creating a bean of {@link WebDriverWait}.
 */
@LazyConfiguration
public class WebDriverWaitConfig {
    @Value("${default.timeout:30}")
    private int timeout;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public WebDriverWait webdriverWait(WebDriver driver) {
        return new WebDriverWait(driver, ofMillis(this.timeout));
    }
}
