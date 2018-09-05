package acceptance.pk.lucidxpo.ynami.cucumber.steps;

import acceptance.pk.lucidxpo.ynami.config.cucumber.AbstractSteps;
import acceptance.pk.lucidxpo.ynami.pageobjects.LoginPage;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.fluentlenium.core.annotation.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginSteps extends AbstractSteps {
    @Page
    private LoginPage loginPage;

    @Before
    public void before() {
        initFluent(webDriver);
    }

    @Given("^I go to login page$")
    public void iGoToLoginPage() {
        loginPage.openPage(port);
    }

    @When("^I enter \"([^\"]*)\" in username field$")
    public void iEnterInUsernameField(final String username) {
        loginPage.username(username);
    }

    @When("^I enter \"([^\"]*)\" in password field$")
    public void iEnterInPasswordField(final String password) {
        loginPage.password(password);
    }

    @When("^I click on submit button$")
    public void iClickOnSubmitButton() {
        loginPage.submit();
    }

    @Then("^I should be logged in$")
    public void iShouldBeLoggedIn() {
        assertEquals("Why Not Me!!! - Admin Demo", getDriver().getTitle());
    }

    @Then("^I should be on login page$")
    public void iShouldBeOnLoginPage() {
        assertEquals("Why Not Me!!! - Login Demo", getDriver().getTitle());
    }
}