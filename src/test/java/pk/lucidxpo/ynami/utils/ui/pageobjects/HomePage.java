package pk.lucidxpo.ynami.utils.ui.pageobjects;

import io.fluentlenium.core.annotation.Page;
import org.openqa.selenium.support.ui.ExpectedCondition;
import pk.lucidxpo.ynami.utils.ui.pageasserts.HomePageAssert;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class HomePage extends BasePage<HomePage, HomePageAssert> {
    @Page
    private LoginPage loginPage;

    public HomePage iAmOnHomePage() {
        this.assertThat().pageLoaded();
        return this;
    }

    public LoginPage logout() {
        clickXpathJs("Logout");
        loginPage.iAmOnLoginPage();
        return loginPage;
    }

    @Override
    public ExpectedCondition<Boolean> getPageLoadCondition() {
        return titleIs("Why Not Me!!! - Admin Demo");
    }

    @Override
    public String getPageUrl() {
        return "/";
    }
}