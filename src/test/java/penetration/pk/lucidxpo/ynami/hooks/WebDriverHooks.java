package penetration.pk.lucidxpo.ynami.hooks;

import io.cucumber.java.AfterAll;

import static penetration.pk.lucidxpo.ynami.web.drivers.DriverFactory.quitAll;

public class WebDriverHooks {
    @AfterAll
    public static void quitWebDriver() {
        quitAll();
    }
}
