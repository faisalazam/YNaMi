package bdd.pk.lucidxpo.ynami.webdriver.config;

import bdd.pk.lucidxpo.ynami.annotations.LazyConfiguration;
import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;
import static java.time.Duration.ofSeconds;

@Profile("!grid")
@LazyConfiguration
public class WebDriverConfig {
    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "firefox")
    public WebDriver firefoxDriver() {
        final WebDriverManager webDriverManager = firefoxdriver();
        final FirefoxOptions options = new FirefoxOptions();
        final Proxy proxy = new Proxy();
        proxy.setAutodetect(false);
        proxy.setNoProxy("no_proxy-var");
        options.setCapability("proxy", proxy);
        webDriverManager
                .capabilities(options)
                .setup();
        return webDriverManager.create();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "edge")
    public WebDriver edgeDriver() {
        return new EdgeDriver();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = "browser", havingValue = "chrome")
    public WebDriver chromeDriver() {
        final WebDriverManager webDriverManager = chromedriver();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--test-type");
        options.setAcceptInsecureCerts(true);
        webDriverManager
                .capabilities(options)
                .setup();
        return webDriverManager.create();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "browser", havingValue = "htmlunit")
    public WebDriver htmlUnitDriver() {
        final WebDriver driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(ofSeconds(2));
        return driver;
    }
}
