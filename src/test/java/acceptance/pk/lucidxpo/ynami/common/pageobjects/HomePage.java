package acceptance.pk.lucidxpo.ynami.common.pageobjects;

import acceptance.pk.lucidxpo.ynami.common.pageasserts.HomePageAssert;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class HomePage extends BasePage<HomePage, HomePageAssert> {
    public void logout() {
        clickXpathJs("Logout");
        assertEquals("Why Not Me!!! - Login Demo", pageTitle());
    }

    @Override
    protected ExpectedCondition getPageLoadCondition() {
        return titleIs("Why Not Me!!! - Admin Demo");
    }

    @Override
    public String getPageUrl() {
        return "/";
    }
}