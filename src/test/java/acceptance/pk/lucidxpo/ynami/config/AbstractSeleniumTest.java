package acceptance.pk.lucidxpo.ynami.config;

import acceptance.pk.lucidxpo.ynami.TestApplication;
import acceptance.pk.lucidxpo.ynami.webdriver.hooks.TestMethodScopeExecutionListener;
import io.fluentlenium.adapter.junit.jupiter.FluentTest;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import pk.lucidxpo.ynami.YNaMiApplication;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static acceptance.pk.lucidxpo.ynami.config.AbstractSeleniumTest.SCHEMA_NAME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@TestPropertySource(properties = {"spring.datasource.name=" + SCHEMA_NAME})
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@SpringBootTest(classes = SeleniumTestCaseContext.class, webEnvironment = DEFINED_PORT)
@TestExecutionListeners(value = TestMethodScopeExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
public abstract class AbstractSeleniumTest extends FluentTest {
    @SuppressWarnings("WrongPropertyKeyValueDelimiter")
    static final String SCHEMA_NAME = "AcceptanceTestSchema";

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