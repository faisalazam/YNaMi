package pk.lucidxpo.ynami.acceptance.cucumber.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import org.fluentlenium.core.annotation.Page;
import pk.lucidxpo.ynami.acceptance.config.cucumber.AbstractSteps;
import pk.lucidxpo.ynami.acceptance.pageobjects.HomePage;

public class LogoutSteps extends AbstractSteps {
    @Page
    private HomePage homePage;

    @Before
    public void before() {
        initFluent(webDriver);
    }

    @When("^I can click on logout$")
    public void iCanClickOnLogout() {
        homePage.logout();
    }
}