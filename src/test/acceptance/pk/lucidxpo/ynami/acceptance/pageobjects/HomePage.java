package pk.lucidxpo.ynami.acceptance.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

@PageObject
public class HomePage extends BasePage<HomePage> {
    @Autowired
    public HomePage(final WebDriver webDriver) {
        super(webDriver);
    }

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