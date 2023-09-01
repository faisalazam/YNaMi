package bdd.pk.lucidxpo.ynami.steps;

import bdd.pk.lucidxpo.ynami.config.AbstractSteps;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.fluentlenium.core.annotation.Page;
import pk.lucidxpo.ynami.utils.ui.pageobjects.LoginPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginSteps extends AbstractSteps {
    @Page
    private LoginPage loginPage;

    @Before
    public void before() {
        initFluent(webDriver);
    }

    @Given("I go to login page")
    public void iGoToLoginPage() {
        loginPage.openPage(port);
    }

    @When("^I enter \"([^\"]*)\" in username field$")
    public void iEnterInUsernameField(final String username) {
        loginPage.username(username);
    }

    @And("^I enter \"([^\"]*)\" in password field$")
    public void iEnterInPasswordField(final String password) {
        loginPage.password(password);
    }

    @And("I click on submit button")
    public void iClickOnSubmitButton() {
        loginPage.submit();
    }

    @Then("I should be logged in")
    public void iShouldBeLoggedIn() {
        assertEquals("Why Not Me!!! - Admin Demo", getDriver().getTitle());
    }

    @Then("I should be on login page")
    public void iShouldBeOnLoginPage() {
        assertEquals("Why Not Me!!! - Login Demo", getDriver().getTitle());
    }
}



