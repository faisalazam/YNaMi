package acceptance.pk.lucidxpo.ynami.cucumber.steps;

import acceptance.pk.lucidxpo.ynami.common.pageobjects.HomePage;
import acceptance.pk.lucidxpo.ynami.cucumber.config.AbstractSteps;
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