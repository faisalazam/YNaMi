package acceptance.pk.lucidxpo.ynami.cucumber.steps;

import acceptance.pk.lucidxpo.ynami.common.pageobjects.HomePage;
import acceptance.pk.lucidxpo.ynami.cucumber.config.AbstractSteps;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import org.fluentlenium.core.annotation.Page;

public class LogoutSteps extends AbstractSteps {
    @Page
    private HomePage homePage;

    @Before
    public void before() {
        initFluent(webDriver);
    }

    @When("I can click on logout")
    public void iCanClickOnLogout() {
        homePage.logout();
    }
}