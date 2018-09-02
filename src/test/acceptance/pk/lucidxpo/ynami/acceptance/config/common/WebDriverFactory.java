package pk.lucidxpo.ynami.acceptance.config.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static java.util.concurrent.TimeUnit.SECONDS;

public class WebDriverFactory {

    public static WebDriver getDriver() {
        final WebDriver driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(2, SECONDS);
        return driver;
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
