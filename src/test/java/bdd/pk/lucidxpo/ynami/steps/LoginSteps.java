package bdd.pk.lucidxpo.ynami.steps;

import bdd.pk.lucidxpo.ynami.config.AbstractSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.fluentlenium.core.annotation.Page;
import pk.lucidxpo.ynami.utils.ui.pageobjects.HomePage;
import pk.lucidxpo.ynami.utils.ui.pageobjects.LoginPage;

public class LoginSteps extends AbstractSteps {
    @Page
    private HomePage homePage;
    @Page
    private LoginPage loginPage;

    @Given("I go to login page")
    public void iGoToLoginPage() {
        loginPage.openPage(port);
    }

    @When("I enter {string} in username field")
    public void iEnterInUsernameField(final String username) {
        loginPage.username(username);
    }

    @And("I enter {string} in password field")
    public void iEnterInPasswordField(final String password) {
        loginPage.password(password);
    }

    @And("I click on submit button")
    public void iClickOnSubmitButton() {
        loginPage.submit();
    }

    @Then("I should be logged in")
    public void iShouldBeLoggedIn() {
        homePage.iAmOnHomePage();
    }

    @Then("I should be on login page")
    public void iShouldBeOnLoginPage() {
        loginPage.iAmOnLoginPage();
    }
}
