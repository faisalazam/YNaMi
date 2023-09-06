package pk.lucidxpo.ynami.utils.webdrivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static java.lang.System.getenv;
import static java.time.Duration.ofSeconds;

public class ChromeWebDriverBuilder {
    private final static int TIMEOUT = 10;
    private static final String CI = "CI";
    private static final String PROXY = "proxy";
    private static final String HEADLESS = "--headless";
    private static final String TEST_TYPE = "--test-type";
    private static final String ENVIRONMENT = "environment";
    private static final String NO_SANDBOX = "--no-sandbox";
    private static final String START_MAXIMIZED = "--start-maximized";
    private static final String DISABLE_DEV_SHM_USAGE = "--disable-dev-shm-usage";

    public static final String HEADLESS_MODE = "headless_mode";

    private final ChromeOptions options;

    public ChromeWebDriverBuilder() {
        options = new ChromeOptions();
        options.addArguments(
                TEST_TYPE,
                NO_SANDBOX,
                START_MAXIMIZED,
                DISABLE_DEV_SHM_USAGE
        );
    }

    public WebDriver build() {
        final String environment = getenv(ENVIRONMENT);
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
        final WebDriver driver = webDriverManager.create();
        driver.manage().timeouts().implicitlyWait(ofSeconds(TIMEOUT));
        return driver;
    }

    public ChromeWebDriverBuilder withAcceptInsecureCerts(final boolean acceptInsecureCerts) {
        options.setAcceptInsecureCerts(acceptInsecureCerts);
        return this;
    }

    public ChromeWebDriverBuilder withProxy(final Proxy proxy) {
        options.setProxy(proxy);
// TODO see if need to set capability as well for Proxy setting
//        options.setCapability(PROXY, proxy);
        return this;
    }

    public ChromeWebDriverBuilder withHeadlessMode(final boolean headlessMode) {
        if (headlessMode) {
            options.addArguments(HEADLESS);
        }
        return this;
    }
}
