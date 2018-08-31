package pk.lucidxpo.ynami.acceptance.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

@PageObject
public class LoginPage extends BasePage<LoginPage> {

    @FindBy(id = "login-username")
    private WebElement username;
    @FindBy(id = "login-password")
    private WebElement password;

    @FindBy(id = "login-btn")
    private WebElement submitButton;

    @Autowired
    public LoginPage(final WebDriver webDriver) {
        super(webDriver);
    }

    public LoginPage username(final String text) {
        username.clear();
        username.sendKeys(text);
        return this;
    }

    public LoginPage password(final String text) {
        password.clear();
        password.sendKeys(text);
        return this;
    }

    public LoginPage submit() {
        submitButton.click();
        assertEquals("Why Not Me!!! - Admin Demo", pageTitle());
        return this;
    }

    public void doLogin(final String testUser, final String testPass) {
        editText("login-username", testUser);
        editText("login-password", testPass);
        clickId("login-btn");
//        assertThat(hasErrors(), is(false));
        assertEquals("Why Not Me!!! - Admin Demo", pageTitle());
    }

    @Override
    protected ExpectedCondition getPageLoadCondition() {
        return titleIs("Why Not Me!!! - Login Demo");
    }

    @Override
    public String getPageUrl() {
        return "/login";
    }
}