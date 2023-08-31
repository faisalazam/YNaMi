package bdd.pk.lucidxpo.ynami.webdriver.hooks;

import bdd.pk.lucidxpo.ynami.annotations.LazyAutowired;
import io.cucumber.java.After;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;

/**
 * Hooks for managing the lifecycle of {@link WebDriver}.
 */
public class WebDriverHooks {
    @LazyAutowired
    private ApplicationContext applicationContext;

    @After
    public void afterScenario() {
        final WebDriver webDriver = this.applicationContext.getBean(WebDriver.class);
        webDriver.quit();
    }
}
