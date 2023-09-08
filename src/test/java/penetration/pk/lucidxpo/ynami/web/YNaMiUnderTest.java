package penetration.pk.lucidxpo.ynami.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import penetration.pk.lucidxpo.ynami.behaviours.ILogin;
import penetration.pk.lucidxpo.ynami.behaviours.ILogout;
import penetration.pk.lucidxpo.ynami.behaviours.INavigable;
import penetration.pk.lucidxpo.ynami.model.Credentials;
import penetration.pk.lucidxpo.ynami.model.UserPassCredentials;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static penetration.pk.lucidxpo.ynami.config.Config.getInstance;
import static pk.lucidxpo.ynami.spring.security.SecurityConfig.LOGIN_PAGE_URL;

public class YNaMiUnderTest extends WebApplication implements ILogin, ILogout, INavigable {
    @Override
    public void navigate() {
        openLoginPage();
        login(getInstance().getDefaultCredentials());
    }

    @Override
    public void openLoginPage() {
        //noinspection ConstantValue
        driver.get(
                getInstance().getBaseUrl()
                        + (LOGIN_PAGE_URL.startsWith("/") ? LOGIN_PAGE_URL.substring(1) : LOGIN_PAGE_URL)
        );
        findAndWaitForElement(id("login-username"));
    }

    @Override
    public void login(final Credentials credentials) {
        final UserPassCredentials userPassCredentials = new UserPassCredentials(credentials);
        driver.findElement(id("login-username")).clear();
        driver.findElement(id("login-username")).sendKeys(userPassCredentials.getUsername());
        driver.findElement(id("login-password")).clear();
        driver.findElement(id("login-password")).sendKeys(userPassCredentials.getPassword());
        driver.findElement(id("login-btn")).click();
    }

    @Override
    public boolean isLoggedIn() {
        return "Why Not Me!!! - Admin Demo".equals(driver.getTitle());
    }

    @Override
    public void logout() {
        final WebElement button = driver.findElement(xpath("//a[contains(text(), 'Logout')]"));
        final JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", button);
    }
}

