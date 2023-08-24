package acceptance.pk.lucidxpo.ynami.selenium.test;

import acceptance.pk.lucidxpo.ynami.common.pageobjects.LoginPage;
import acceptance.pk.lucidxpo.ynami.selenium.config.AbstractSeleniumTest;
import org.fluentlenium.core.annotation.Page;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@EnabledIf(value = "${togglz.features.WEB_SECURITY.enabled:false}", loadContext = true)
@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class LoginSeleniumTest extends AbstractSeleniumTest {
    @Page
    private LoginPage loginPage;

    @Test
    void shouldVerifySuccessfulLoginAndLogoutWithCorrectCredentialsForAdminUser() {
        loginPage.openPage(port).username("admin").password("admin").submit().logout();
    }

    @Test
    void shouldVerifySuccessfulLoginAndLogoutWithCorrectCredentialsForSupportUser() {
        loginPage.openPage(port).doLogin("support", "support").logout();
    }

    @Test
    void shouldVerifySuccessfulLoginAndLogoutWithCorrectCredentialsForUser() {
        loginPage.openPage(port).doLogin("user", "user").logout();
    }
}