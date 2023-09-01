package bdd.pk.lucidxpo.ynami.steps;

import bdd.pk.lucidxpo.ynami.config.AbstractSteps;
import io.cucumber.java.en.When;
import org.fluentlenium.core.annotation.Page;
import pk.lucidxpo.ynami.utils.ui.pageobjects.HomePage;

public class LogoutSteps extends AbstractSteps {
    @Page
    private HomePage homePage;

    @When("I can click on logout")
    public void iCanClickOnLogout() {
        homePage.logout();
    }
}