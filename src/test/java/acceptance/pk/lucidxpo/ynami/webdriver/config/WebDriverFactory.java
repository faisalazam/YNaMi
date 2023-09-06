package acceptance.pk.lucidxpo.ynami.webdriver.config;

import org.openqa.selenium.WebDriver;
import pk.lucidxpo.ynami.utils.webdrivers.ChromeWebDriverBuilder;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;
import static java.lang.System.getenv;
import static pk.lucidxpo.ynami.utils.webdrivers.ChromeWebDriverBuilder.HEADLESS_MODE;

public class WebDriverFactory {
    public static WebDriver getDriver() {
        final String headlessMode = getenv(HEADLESS_MODE);
        final boolean runInHeadlessMode = headlessMode == null || TRUE.equals(valueOf(headlessMode));
        return new ChromeWebDriverBuilder()
                .withAcceptInsecureCerts(true)
                .withHeadlessMode(runInHeadlessMode)
                .build();
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
