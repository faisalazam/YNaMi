package acceptance.pk.lucidxpo.ynami.cucumber.steps;

import acceptance.pk.lucidxpo.ynami.config.cucumber.AbstractSteps;
import acceptance.pk.lucidxpo.ynami.pageobjects.LoginPage;
import io.cucumber.java.Before;
import io.cucumber.java8.En;
import org.fluentlenium.core.annotation.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginSteps extends AbstractSteps implements En {
    @Page
    private LoginPage loginPage;

    @Before
    public void before() {
        initFluent(webDriver);
    }

    public LoginSteps() {
        Given("^I go to login page$", () -> {
            loginPage.openPage(port);
        });

        When("^I enter \"([^\"]*)\" in username field$", (final String username) -> {
            loginPage.username(username);
        });

        And("^I enter \"([^\"]*)\" in password field$", (final String password) -> {
            loginPage.password(password);
        });

        And("^I click on submit button$", () -> {
            loginPage.submit();
        });

        Then("^I should be logged in$", () -> {
            assertEquals("Why Not Me!!! - Admin Demo", getDriver().getTitle());
        });

        Then("^I should be on login page$", () -> {
            assertEquals("Why Not Me!!! - Login Demo", getDriver().getTitle());
        });
    }
}