package penetration.pk.lucidxpo.ynami.config.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import penetration.pk.lucidxpo.ynami.TestApplication;
import pk.lucidxpo.ynami.YNaMiApplication;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static penetration.pk.lucidxpo.ynami.config.cucumber.CucumberContextConfigurationLoader.SCHEMA_NAME;

@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestPropertySource(properties = {"spring.datasource.name=" + SCHEMA_NAME})
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
public class CucumberContextConfigurationLoader {
    @SuppressWarnings("WrongPropertyKeyValueDelimiter")
    static final String SCHEMA_NAME = "SecurityTestSchema";
}