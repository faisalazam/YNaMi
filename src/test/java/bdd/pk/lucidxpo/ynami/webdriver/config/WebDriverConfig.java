package bdd.pk.lucidxpo.ynami.webdriver.config;

import bdd.pk.lucidxpo.ynami.annotations.LazyConfiguration;
import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.edgedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;
import static java.lang.System.getenv;
import static java.time.Duration.ofSeconds;

@Profile("!grid")
@LazyConfiguration
public class WebDriverConfig {
    private static final String CI = "CI";
    private static final String EDGE = "edge";
    private static final String PROXY = "proxy";
    private static final String CHROME = "chrome";
    private static final String BROWSER = "browser";
    private static final String MAC_VERSION = "116";
    private static final String FIREFOX = "firefox";
    private static final String LINUX_VERSION = "114";
    private static final String HTMLUNIT = "htmlunit";
    private static final String HEADLESS = "--headless";
    private static final String TEST_TYPE = "--test-type";
    private static final String ENVIRONMENT = "environment";
    private static final String NO_SANDBOX = "--no-sandbox";
    private static final String NO_PROXY_VAR = "no_proxy-var";
    private static final String HEADLESS_MODE = "headless_mode";
    private static final String DISABLE_DEV_SHM_USAGE = "--disable-dev-shm-usage";

    @Primary
    @WebdriverBeanScope
    @ConditionalOnProperty(name = BROWSER, havingValue = FIREFOX)
    public WebDriver firefoxDriver() {
        final WebDriverManager webDriverManager = firefoxdriver();
        final FirefoxOptions options = new FirefoxOptions();
        final Proxy proxy = new Proxy();
        proxy.setAutodetect(false);
        proxy.setNoProxy(NO_PROXY_VAR);
        options.setCapability(PROXY, proxy);
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
        final WebDriverManager webDriverManager = chromedriver();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments(TEST_TYPE);
        options.addArguments(NO_SANDBOX);
        options.setAcceptInsecureCerts(true);
        options.addArguments(DISABLE_DEV_SHM_USAGE);

        // default setting will be headless mode
        final String headlessMode = getenv(HEADLESS_MODE);
        if (headlessMode == null || TRUE.equals(valueOf(headlessMode))) {
            options.addArguments(HEADLESS);
        }

        setChromeVersion(webDriverManager);
        webDriverManager
                .capabilities(options)
                .setup();
        return webDriverManager.create();
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

    private static void setChromeVersion(final WebDriverManager webDriverManager) {
        final String environment = getenv(ENVIRONMENT);
        if (CI.equalsIgnoreCase(environment)) {
            webDriverManager
                    .driverVersion(LINUX_VERSION)
//                    .browserVersion(LINUX_VERSION)
            ;
        } else {
            webDriverManager
                    .driverVersion(MAC_VERSION)
//                    .browserVersion(MAC_VERSION)
            ;
        }
    }
}
