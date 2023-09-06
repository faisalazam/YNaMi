package bdd.pk.lucidxpo.ynami.webdriver.config;

import bdd.pk.lucidxpo.ynami.annotations.LazyConfiguration;
import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pk.lucidxpo.ynami.utils.webdrivers.ChromeWebDriverBuilder;

import static io.github.bonigarcia.wdm.WebDriverManager.edgedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;
import static java.lang.System.getenv;
import static java.time.Duration.ofSeconds;
import static pk.lucidxpo.ynami.utils.webdrivers.ChromeWebDriverBuilder.HEADLESS_MODE;

@Profile("!grid")
@LazyConfiguration
public class WebDriverConfig {
    private static final String EDGE = "edge";
    private static final String PROXY = "proxy";
    private static final String CHROME = "chrome";
    private static final String BROWSER = "browser";
    private static final String FIREFOX = "firefox";
    private static final String HTMLUNIT = "htmlunit";
    private static final String NO_PROXY_VAR = "no_proxy-var";

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = BROWSER, havingValue = FIREFOX)
    public WebDriver firefoxDriver() {
        final Proxy proxy = new Proxy();
        proxy.setAutodetect(false);
        proxy.setNoProxy(NO_PROXY_VAR);

        final FirefoxOptions options = new FirefoxOptions();
        options.setCapability(PROXY, proxy);

        final WebDriverManager webDriverManager = firefoxdriver();
        webDriverManager
                .capabilities(options)
                .setup();
        return webDriverManager.create();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = BROWSER, havingValue = EDGE)
    public WebDriver edgeDriver() {
        final WebDriverManager webDriverManager = edgedriver();
        webDriverManager.setup();
        return webDriverManager.create();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = BROWSER, havingValue = CHROME)
    public WebDriver chromeDriver() {
        final String headlessMode = getenv(HEADLESS_MODE);
        final boolean runInHeadlessMode = headlessMode == null || TRUE.equals(valueOf(headlessMode));
        return new ChromeWebDriverBuilder()
                .withAcceptInsecureCerts(true)
                .withHeadlessMode(runInHeadlessMode)
                .build();
    }

    @Primary
    @WebdriverBeanScope
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = BROWSER, havingValue = HTMLUNIT)
    public WebDriver htmlUnitDriver() {
        final WebDriver driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(ofSeconds(2));
        return driver;
    }
}
