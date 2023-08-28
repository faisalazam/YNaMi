package pk.lucidxpo.ynami.utils.executionlisteners;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import javax.sql.DataSource;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

public class DatabaseExecutionListener implements TestExecutionListener {
    private static final String FLYWAY_SCHEMA_HISTORY = "flyway_schema_history";

    @Override
    public void beforeTestClass(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        cleanDBData(testContext);
    }

    @Override
    public void afterTestMethod(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        cleanDBData(testContext);
    }

    private void cleanDBData(final TestContext testContext) {
        final DataSource dataSource = testContext.getApplicationContext().getBean(DataSource.class);
        final JdbcTemplate jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);
        cleanDBData(dataSource, jdbcTemplate);
    }

    private void cleanDBData(final DataSource dataSource, final JdbcTemplate jdbcTemplate) {
        // The next few statements will find all the tables in our db schema
        // Without being specific and not specifying catalog/schema names, will sometimes result in
        // issues. For example, when root user is used to connect to db, it might also get other tables like
        // sys_config and later on complain that it can't delete from there...
        // Catalog/Schema names can be hard-coded as well...
        final String schemaName = dataSource instanceof HikariDataSource
                ? ((HikariDataSource) dataSource).getPoolName()
                : null;

        // The query ["SHOW TABLES FROM " + schemaName] could have been used too, but it has different structure of the
        // result set for both the H2 and MySql, which will add unnecessary extra logic to make it work for both.
        final String query = "SELECT TABLE_NAME\n" +
                "FROM INFORMATION_SCHEMA.TABLES\n" +
                "WHERE TABLE_SCHEMA = '" + schemaName + "'";
        final Set<String> tableNames = jdbcTemplate.queryForList(query, String.class)
                .stream()
                .filter(tableName -> !FLYWAY_SCHEMA_HISTORY.equalsIgnoreCase(tableName))
                .collect(toSet());
        cleanDBData(jdbcTemplate, tableNames);
    }

    private void cleanDBData(final JdbcTemplate jdbcTemplate, final Set<String> tableNames) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;"); // Disable foreign key constraint checks
        deleteFromTables(jdbcTemplate, tableNames.toArray(new String[]{}));
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;"); // Enable foreign key constraint checks
    }
}