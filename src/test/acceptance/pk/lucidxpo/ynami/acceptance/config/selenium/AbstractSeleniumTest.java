package pk.lucidxpo.ynami.acceptance.config.selenium;

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
@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestExecutionListeners(value = SeleniumTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
public abstract class AbstractSeleniumTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected WebDriver webDriver;

    @Autowired
    protected FeatureManagerWrappable featureManager;
}