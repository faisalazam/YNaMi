package bdd.pk.lucidxpo.ynami.webdriver.config;

import bdd.pk.lucidxpo.ynami.annotations.LazyConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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
 * This class is for auto-wiring the {@link WebDriverWait} instance in the tests. The default timeout duration is set
 * as 30 seconds, and for parallel test execution, the bean is annotated with
 * {@link Scope(ConfigurableBeanFactory#SCOPE_PROTOTYPE)}. A bean with the prototype scope will return a different
 * instance every time it is requested from the container.
 */
@SuppressWarnings("JavadocReference")
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
