package acceptance.pk.lucidxpo.ynami.webdriver.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;
import static java.lang.System.getenv;

public class WebDriverFactory {
    private static final String TEST_TYPE = "--test-type";
    private static final String HEADLESS = "--headless=new";
    private static final String NO_SANDBOX = "--no-sandbox";
    private static final String HEADLESS_MODE = "headless_mode";
    private static final String START_MAXIMIZED = "--start-maximized";
    private static final String DISABLE_DEV_SHM_USAGE = "--disable-dev-shm-usage";

    public static WebDriver getDriver() {
        final WebDriverManager webDriverManager = chromedriver();
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
        webDriverManager
                .capabilities(options)
                .setup();
        return webDriverManager.create();
    }

//    private WebDriverFactory() {
//        // Do-nothing..Do not allow to initialize this class from outside
//    }
//
//    private static final WebDriverFactory instance = new WebDriverFactory();
//
//    public static WebDriverFactory getInstance() {
//        return instance;
//    }
//
//    private final ThreadLocal<WebDriver> driver = withInitial(() -> new HtmlUnitDriver(true));
//
//    public WebDriver getDriver() {
//        return driver.get();
//    }
//
//    public void removeDriver() {
//        getDriver().quit();
//        driver.remove();
//    }
}
