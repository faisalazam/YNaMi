package pk.lucidxpo.ynami.acceptance.selenium.test;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.acceptance.config.selenium.AbstractSeleniumTest;
import pk.lucidxpo.ynami.acceptance.pageobjects.LoginPage;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class LoginSeleniumTest extends AbstractSeleniumTest {
    @Test
    void shouldVerifySuccessfulLoginWithCorrectCredentialsForAdminUser() {
        assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

        final LoginPage loginPage = new LoginPage(webDriver).openPage(port);
        loginPage.username("admin").password("admin").submit();
    }

    @Test
    void shouldVerifySuccessfulLoginWithCorrectCredentialsForSupportUser() {
        assumeTrue(featureManager.isActive(WEB_SECURITY), "Test is ignored as Web Security is disabled");

        final LoginPage loginPage = new LoginPage(webDriver).openPage(port);
        loginPage.doLogin("support", "support");
    }
}