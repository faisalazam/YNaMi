package pk.lucidxpo.ynami.acceptance.config.selenium;

import org.fluentlenium.adapter.junit.jupiter.FluentTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@ExtendWith(SpringExtension.class)
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