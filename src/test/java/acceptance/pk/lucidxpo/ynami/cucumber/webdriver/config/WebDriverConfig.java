package acceptance.pk.lucidxpo.ynami.cucumber.webdriver.config;

import acceptance.pk.lucidxpo.ynami.cucumber.annotations.LazyConfiguration;
import acceptance.pk.lucidxpo.ynami.cucumber.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("!grid")
@LazyConfiguration
public class WebDriverConfig {
    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "firefox")
    public WebDriver firefoxDriver() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        final Proxy proxy = new Proxy();
        proxy.setAutodetect(false);
        proxy.setNoProxy("no_proxy-var");
        firefoxOptions.setCapability("proxy", proxy);
        return new FirefoxDriver(firefoxOptions);
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "edge")
    public WebDriver edgeDriver() {
        return new EdgeDriver();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "browser", havingValue = "chrome")
    public WebDriver chromeDriver() {
//        WebDriverManager.chromedriver().setup();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--test-type");
        options.setAcceptInsecureCerts(true);
        return new ChromeDriver(options);
    }
}
