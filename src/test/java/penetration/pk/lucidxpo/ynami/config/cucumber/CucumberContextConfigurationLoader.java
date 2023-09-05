package penetration.pk.lucidxpo.ynami.config.cucumber;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import penetration.pk.lucidxpo.ynami.TestApplication;
import pk.lucidxpo.ynami.YNaMiApplication;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.util.stream.Stream.of;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static penetration.pk.lucidxpo.ynami.config.cucumber.CucumberContextConfigurationLoader.SCHEMA_NAME;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestPropertySource(properties = {"spring.datasource.name=" + SCHEMA_NAME})
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
public class CucumberContextConfigurationLoader {
    @SuppressWarnings("WrongPropertyKeyValueDelimiter")
    static final String SCHEMA_NAME = "SecurityTestSchema";

    protected static Connection connection;
    protected static ApplicationContext applicationContext;

    // TODO Spring Upgrade - It breaks cucumber in acceptance package, but why????
    // Step failed
    // io.cucumber.core.exception.CucumberException: Could not invoke hook defined at
    // 'penetration.pk.lucidxpo.ynami.config.cucumber.CucumberContextConfigurationLoader.loadContext()'.
    @Before
    public void loadContext() {
        // Dummy method so cucumber will recognize this class as glue
        // and use its context configuration.
    }

    // TODO Spring Upgrade - can we replace this method with @Sql class level annotation???
    @Autowired
    public void populateData(final DataSource dataSource) throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
        }
        of("insert-roles.sql", "insert-users.sql")
                .forEach(sqlScript ->
                        executeSqlScript(connection, new ClassPathResource(sqlScript))
                );
    }

    @Autowired
    public void setApplicationContext(final ApplicationContext applicationContext) {
        CucumberContextConfigurationLoader.applicationContext = applicationContext;
    }
}