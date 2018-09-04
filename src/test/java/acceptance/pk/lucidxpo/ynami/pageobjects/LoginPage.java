package acceptance.pk.lucidxpo.ynami.pageobjects;

import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class LoginPage extends BasePage<LoginPage> {
    @Page
    private HomePage homePage;

    @FindBy(id = "login-username")
    private FluentWebElement username;
    @FindBy(id = "login-password")
    private FluentWebElement password;
    @FindBy(id = "login-btn")
    private FluentWebElement submitButton;

    public LoginPage username(final String text) {
        username.clear();
        username.fill().with(text);
        return this;
    }

    public LoginPage password(final String text) {
        password.clear();
        password.fill().with(text);
        return this;
    }

    public HomePage submit() {
        submitButton.click();
        waitForPageToLoad(homePage.getPageLoadCondition());
        return homePage;
    }

    public HomePage doLogin(final String testUser, final String testPass) {
        editText("login-username", testUser);
        editText("login-password", testPass);
        clickId("login-btn");
//        assertThat(hasErrors(), is(false));
        waitForPageToLoad(homePage.getPageLoadCondition());
        return homePage;
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