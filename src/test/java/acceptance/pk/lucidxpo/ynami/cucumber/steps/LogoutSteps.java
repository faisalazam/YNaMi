package acceptance.pk.lucidxpo.ynami.cucumber.steps;

import acceptance.pk.lucidxpo.ynami.config.cucumber.AbstractSteps;
import acceptance.pk.lucidxpo.ynami.pageobjects.HomePage;
import io.cucumber.java.Before;
import io.cucumber.java8.En;
import org.fluentlenium.core.annotation.Page;

public class LogoutSteps extends AbstractSteps implements En {
    @Page
    private HomePage homePage;

    @Before
    public void before() {
        initFluent(webDriver);
    }

    public LogoutSteps() {
        When("^I can click on logout$", () -> {
            homePage.logout();
        });
    }
}