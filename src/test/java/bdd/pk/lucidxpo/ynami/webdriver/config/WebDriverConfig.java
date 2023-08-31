package bdd.pk.lucidxpo.ynami.webdriver.config;

import bdd.pk.lucidxpo.ynami.annotations.LazyConfiguration;
import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static java.time.Duration.ofSeconds;

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
    @ConditionalOnProperty(name = "browser", havingValue = "chrome")
    public WebDriver chromeDriver() {
//        WebDriverManager.chromedriver().setup();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--test-type");
        options.setAcceptInsecureCerts(true);
        return new ChromeDriver(options);
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "browser", havingValue = "htmlUnit")
    public WebDriver htmlUnitDriver() {
        final WebDriver driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(ofSeconds(2));
        return driver;
    }
}
