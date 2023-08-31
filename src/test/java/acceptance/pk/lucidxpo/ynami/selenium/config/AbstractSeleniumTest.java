package acceptance.pk.lucidxpo.ynami.selenium.config;

import acceptance.pk.lucidxpo.ynami.TestApplication;
import acceptance.pk.lucidxpo.ynami.selenium.config.scope.SeleniumTestExecutionListener;
import org.fluentlenium.adapter.junit.jupiter.FluentTest;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import pk.lucidxpo.ynami.YNaMiApplication;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@SpringBootTest(classes = SeleniumTestCaseContext.class, webEnvironment = DEFINED_PORT)
@TestExecutionListeners(value = SeleniumTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
public abstract class AbstractSeleniumTest extends FluentTest {
    @LocalServerPort
    protected int port;

    @Autowired
    private WebDriver webDriver;

    @Override
    public WebDriver newWebDriver() {
        return webDriver;
    }

    @Autowired
    protected FeatureManagerWrappable featureManager;
}