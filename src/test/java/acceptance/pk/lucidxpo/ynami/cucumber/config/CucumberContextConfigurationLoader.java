package acceptance.pk.lucidxpo.ynami.cucumber.config;

import acceptance.pk.lucidxpo.ynami.TestApplication;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pk.lucidxpo.ynami.YNaMiApplication;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.util.stream.Stream.of;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@SuppressWarnings("NewClassNamingConvention")
//@Profile("at")
@ExtendWith(SpringExtension.class)
@CucumberContextConfiguration
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@SpringBootTest(classes = CucumberTestCaseContext.class, webEnvironment = DEFINED_PORT)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
public class CucumberContextConfigurationLoader {
    protected static Connection connection;
    protected static ApplicationContext applicationContext;

    @Before
    public void loadContext() {
        // Dummy method so cucumber will recognize this class as glue
        // and use its context configuration.
    }

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