package acceptance.pk.lucidxpo.ynami.cucumber.webdriver.config;

import acceptance.pk.lucidxpo.ynami.cucumber.annotations.LazyConfiguration;
import acceptance.pk.lucidxpo.ynami.cucumber.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.net.URL;

@Profile("grid")
@LazyConfiguration
public class RemoteWebDriverConfig {
    @Value("${selenium.grid.url}")
    private URL url;

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "firefox")
    public WebDriver remoteFirefoxDriver() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        return new RemoteWebDriver(this.url, firefoxOptions);
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "edge")
    public WebDriver remoteEdgeDriver() {
        final EdgeOptions edgeOptions = new EdgeOptions();
        return new RemoteWebDriver(this.url, edgeOptions);
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "browser", havingValue = "chrome")
    public WebDriver remoteChromeDriver() {
        final ChromeOptions chromeOptions = new ChromeOptions();
        return new RemoteWebDriver(this.url, chromeOptions);
    }
}
