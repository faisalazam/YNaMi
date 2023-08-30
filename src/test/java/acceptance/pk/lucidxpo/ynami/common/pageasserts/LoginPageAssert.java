package acceptance.pk.lucidxpo.ynami.common.pageasserts;

import acceptance.pk.lucidxpo.ynami.common.pageobjects.LoginPage;
import org.assertj.core.api.AbstractAssert;

public class LoginPageAssert extends AbstractAssert<LoginPageAssert, LoginPage> {
    private final LoginPage loginPage;

    public LoginPageAssert(final LoginPage loginPage) {
        super(loginPage, LoginPageAssert.class);
        this.loginPage = loginPage;
    }

    public void pageLoaded() {
        loginPage.waitForPageToLoad(loginPage.getPageLoadCondition());
    }
}