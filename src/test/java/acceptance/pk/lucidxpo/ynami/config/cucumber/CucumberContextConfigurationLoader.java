package acceptance.pk.lucidxpo.ynami.config.cucumber;

import acceptance.pk.lucidxpo.ynami.TestApplication;
import cucumber.api.java.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pk.lucidxpo.ynami.YNaMiApplication;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@SpringBootTest(classes = CucumberTestCaseContext.class, webEnvironment = DEFINED_PORT)
public class CucumberContextConfigurationLoader {
    protected static ApplicationContext applicationContext;

    @Before
    public void loadContext() {
        // Dummy method so cucumber will recognize this class as glue
        // and use its context configuration.
    }

    @Autowired
    public void setApplicationContext(final ApplicationContext applicationContext) {
        CucumberContextConfigurationLoader.applicationContext = applicationContext;
    }
}