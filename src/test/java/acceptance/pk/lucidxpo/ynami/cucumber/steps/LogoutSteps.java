package acceptance.pk.lucidxpo.ynami.cucumber.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import org.fluentlenium.core.annotation.Page;
import acceptance.pk.lucidxpo.ynami.config.cucumber.AbstractSteps;
import acceptance.pk.lucidxpo.ynami.pageobjects.HomePage;

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