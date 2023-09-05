package bdd.pk.lucidxpo.ynami.webdriver.config;

import bdd.pk.lucidxpo.ynami.annotations.LazyConfiguration;
import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
    private final static int TIMEOUT = 10;
    private static final String CI = "CI";
    private static final String EDGE = "edge";
    private static final String PROXY = "proxy";
    private static final String CHROME = "chrome";
    private static final String BROWSER = "browser";
    private static final String FIREFOX = "firefox";
    private static final String HTMLUNIT = "htmlunit";
    private static final String HEADLESS = "--headless";
    private static final String TEST_TYPE = "--test-type";
    private static final String ENVIRONMENT = "environment";
    private static final String NO_SANDBOX = "--no-sandbox";
    private static final String NO_PROXY_VAR = "no_proxy-var";
    private static final String HEADLESS_MODE = "headless_mode";
    private static final String START_MAXIMIZED = "--start-maximized";
    private static final String DISABLE_DEV_SHM_USAGE = "--disable-dev-shm-usage";

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
        final String environment = getenv(ENVIRONMENT);
        final ChromeOptions options = getChromeOptions();
        if (CI.equalsIgnoreCase(environment)) {
            // Tests are not passing when the webDriver is managed through WebDriverManager on Linux/local Docker
            // container/GitHub Workflow, but all fine on local macOS. WebDriverManager is good in a way that we don't
            // need to download/setup webdriver path, and it is doing all that fine on all systems, but for some reason
            // it results in a NullPointerException during webElement interactions.
            final WebDriver driver = new ChromeDriver(options);
            // This 'implicitlyWait' is an important statement for the proper functioning of the cucumber tests on linux
            // machine. Tests were failing only on Linux/GitHub Workflow and nothing worked even after trying like million
            // of things. They work fine on my local Mac but may be the other machines or too fast or too slow that they
            // keep failing there. 'implicitlyWait' is here for the rescue finally.
            driver.manage().timeouts().implicitlyWait(ofSeconds(TIMEOUT));
            return driver;
        }

        final WebDriverManager webDriverManager = chromedriver();
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

    private static ChromeOptions getChromeOptions() {
        final ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments(
                TEST_TYPE,
                NO_SANDBOX,
                START_MAXIMIZED,
                DISABLE_DEV_SHM_USAGE
        );

        // default setting will be headless mode
        final String headlessMode = getenv(HEADLESS_MODE);
        if (headlessMode == null || TRUE.equals(valueOf(headlessMode))) {
            options.addArguments(HEADLESS);
        }
        return options;
    }
}
