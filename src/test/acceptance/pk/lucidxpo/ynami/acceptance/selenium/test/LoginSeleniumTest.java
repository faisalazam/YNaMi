package pk.lucidxpo.ynami.acceptance.selenium.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import pk.lucidxpo.ynami.acceptance.config.selenium.AbstractSeleniumTest;
import pk.lucidxpo.ynami.acceptance.pageobjects.LoginPage;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static pk.lucidxpo.ynami.acceptance.config.WebDriverFactory.getDriver;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

class LoginSeleniumTest extends AbstractSeleniumTest {
    @AfterAll
    static void tearDown() {
        getDriver().close();
    }

    @Test
    void shouldVerifySuccessfulLoginWithCorrectCredentialsForAdminUser() {
        assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

        final LoginPage loginPage = new LoginPage().openPage(port);
        loginPage.username("admin").password("admin").submit();
    }

    @Test
    void shouldVerifySuccessfulLoginWithCorrectCredentialsForSupportUser() {
        assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

        final LoginPage loginPage = new LoginPage().openPage(port);
        loginPage.doLogin("support", "support");
    }
}