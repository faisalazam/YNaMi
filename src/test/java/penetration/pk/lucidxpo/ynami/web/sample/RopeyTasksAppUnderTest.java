package penetration.pk.lucidxpo.ynami.web.sample;

import org.openqa.selenium.WebElement;
import penetration.pk.lucidxpo.ynami.behaviours.ILogin;
import penetration.pk.lucidxpo.ynami.behaviours.ILogout;
import penetration.pk.lucidxpo.ynami.behaviours.INavigable;
import penetration.pk.lucidxpo.ynami.model.Credentials;
import penetration.pk.lucidxpo.ynami.model.UserPassCredentials;
import penetration.pk.lucidxpo.ynami.web.WebApplication;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.linkText;
import static org.openqa.selenium.By.name;
import static penetration.pk.lucidxpo.ynami.config.Config.getInstance;

public class RopeyTasksAppUnderTest extends WebApplication implements ILogin, ILogout, INavigable {
    @Override
    public void navigate() {
        openLoginPage();
        login(getInstance().getDefaultCredentials());
        viewProfile();
        search("test");
    }

    @Override
    public void openLoginPage() {
        driver.get(getInstance().getBaseUrl() + "user/login");
        findAndWaitForElement(id("username"));
    }

    @Override
    public void login(final Credentials credentials) {
        final UserPassCredentials userPassCredentials = new UserPassCredentials(credentials);
        driver.findElement(id("username")).clear();
        driver.findElement(id("username")).sendKeys(userPassCredentials.getUsername());
        driver.findElement(id("password")).clear();
        driver.findElement(id("password")).sendKeys(userPassCredentials.getPassword());
        driver.findElement(name("_action_login")).click();
    }

    @Override
    public boolean isLoggedIn() {
        driver.get(getInstance().getBaseUrl() + "task/list");
        return driver.getPageSource().contains("Tasks");
    }

    @Override
    public void logout() {
        driver.findElement(linkText("Logout")).click();
    }

    public void viewAllUsers() {
        driver.get(getInstance().getBaseUrl() + "admin/list");
    }

    public void viewBobsProfile() {
        viewProfile();
    }

    public void viewAlicesProfile() {
        viewProfile();
    }

    private void viewProfile() {
        driver.findElement(linkText("Profile")).click();
    }

    @SuppressWarnings("SameParameterValue")
    private void search(final String query) {
        findAndWaitForElement(linkText("Tasks")).click();
        driver.findElement(id("q")).clear();
        driver.findElement(id("q")).sendKeys(query);
        final WebElement searchBtn = driver.findElement(name("search-button"));
        searchBtn.click();
    }
}

