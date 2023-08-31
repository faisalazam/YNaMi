package pk.lucidxpo.ynami.utils.ui.pageobjects;

import pk.lucidxpo.ynami.utils.ui.pageasserts.HomePageAssert;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class HomePage extends BasePage<HomePage, HomePageAssert> {
    public void logout() {
        clickXpathJs("Logout");
        assertEquals("Why Not Me!!! - Login Demo", pageTitle());
    }

    @Override
    protected ExpectedCondition<Boolean> getPageLoadCondition() {
        return titleIs("Why Not Me!!! - Admin Demo");
    }

    @Override
    public String getPageUrl() {
        return "/";
    }
}