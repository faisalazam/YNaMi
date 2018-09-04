package acceptance.pk.lucidxpo.ynami.config.cucumber;

import acceptance.pk.lucidxpo.ynami.TestApplication;
import org.fluentlenium.adapter.cucumber.FluentCucumberTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pk.lucidxpo.ynami.YNaMiApplication;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@SpringBootTest(classes = CucumberTestCaseContext.class, webEnvironment = DEFINED_PORT)
public abstract class AbstractSteps extends FluentCucumberTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected WebDriver webDriver;

    @Override
    public WebDriver newWebDriver() {
        return webDriver;
    }
}