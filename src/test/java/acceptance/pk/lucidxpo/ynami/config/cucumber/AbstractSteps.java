package acceptance.pk.lucidxpo.ynami.config.cucumber;

import org.fluentlenium.adapter.cucumber.FluentCucumberTest;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

public abstract class AbstractSteps extends FluentCucumberTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected WebDriver webDriver;
}