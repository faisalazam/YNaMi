package pk.lucidxpo.ynami.utils.ui.pageobjects;

import io.fluentlenium.core.annotation.Page;
import io.fluentlenium.core.annotation.PageUrl;
import io.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import pk.lucidxpo.ynami.utils.ui.pageasserts.LoginPageAssert;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

@PageUrl("/login")
public class LoginPage extends BasePage<LoginPage, LoginPageAssert> {
    @Page
    private HomePage homePage;

    // TODO: These @FindBy are not working or intermittently working/failing, see if we can get them working; but for
    //  now, performing actions on elements directly. They result in SearchContext is null even though the WebDriver
    //  is not null.
    @FindBy(id = "login-username")
    private FluentWebElement username;
    @FindBy(id = "login-password")
    private FluentWebElement password;
    @FindBy(id = "login-btn")
    private FluentWebElement submitButton;

    //********* Web Elements by using By Class *********
    private final By usernameBy = id("login-username");
    private final By passwordBy = id("login-password");
    private final By submitButtonBy = id("login-btn");

    public LoginPage username(final String text) {
//        TODO: SEE if @FindBy can be functional
//        username.clear();
//        username.fill().with(text);
        clearAndFill(usernameBy, text);
        return this;
    }

    public LoginPage password(final String text) {
//        password.clear();
//        password.fill().with(text);
        clearAndFill(passwordBy, text);
        return this;
    }

    public HomePage submit() {
//        submitButton.click();
        clickId(submitButtonBy);
        return homePage.iAmOnHomePage();
    }

    public HomePage doLogin(final String testUser, final String testPass) {
        editText(usernameBy, testUser);
        editText(passwordBy, testPass);
        clickId(submitButtonBy);
//        assertThat(hasErrors(), is(false));
        return homePage.iAmOnHomePage();
    }

    public LoginPage iAmOnLoginPage() {
        this.assertThat().pageLoaded();
        return this;
    }

    @Override
    public ExpectedCondition<Boolean> getPageLoadCondition() {
        return titleIs("Why Not Me!!! - Login Demo");
    }
}