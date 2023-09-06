package acceptance.pk.lucidxpo.ynami.webdriver.config;

import org.openqa.selenium.WebDriver;
import pk.lucidxpo.ynami.utils.webdrivers.ChromeWebDriverBuilder;

public class WebDriverFactory {
    public static WebDriver getDriver() {
        return new ChromeWebDriverBuilder().build();
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
