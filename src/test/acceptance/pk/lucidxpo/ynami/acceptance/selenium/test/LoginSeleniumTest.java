package pk.lucidxpo.ynami.acceptance.selenium.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.acceptance.pageobjects.LoginPage;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static pk.lucidxpo.ynami.acceptance.config.WebDriverFactory.getDriver;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@SpringBootTest(webEnvironment = DEFINED_PORT)
class LoginSeleniumTest extends AbstractIntegrationTest {
    @Value("${local.server.port}")
    int port;

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