package pk.lucidxpo.ynami.utils.executionlisteners;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.jdbc.support.JdbcUtils.extractDatabaseMetaData;

public class DatabaseExecutionListener implements TestExecutionListener {
    private static final String HIBERNATE_SEQUENCE = "hibernate_sequence";
    private static final String FLYWAY_SCHEMA_HISTORY = "flyway_schema_history";

    @Override
    public void afterTestMethod(final TestContext testContext) throws MetaDataAccessException, SQLException {
        final DataSource dataSource = testContext.getApplicationContext().getBean(DataSource.class);
        final JdbcTemplate jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);
        cleanDBData(dataSource, jdbcTemplate);
    }

    private void cleanDBData(final DataSource dataSource, final JdbcTemplate jdbcTemplate) throws MetaDataAccessException, SQLException {
        final DatabaseMetaDataCallback action = databaseMetaData -> databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
        final ResultSet resultSet = (ResultSet) extractDatabaseMetaData(dataSource, action);
        jdbcTemplate.batchUpdate(getStrings(resultSet).toArray(new String[]{}));
    }

    private List<String> getStrings(final ResultSet resultSet) throws SQLException {
        final List<String> statements = newArrayList("SET FOREIGN_KEY_CHECKS = 0;");
        while (resultSet.next()) {
            final String tableName = resultSet.getString("TABLE_NAME");
            addDeleteTableStatement(statements, tableName);
        }
        statements.add("SET FOREIGN_KEY_CHECKS = 1;");
        return statements;
    }

    private void addDeleteTableStatement(final List<String> statements, final String tableName) {
        if (!(FLYWAY_SCHEMA_HISTORY.equalsIgnoreCase(tableName)
                || HIBERNATE_SEQUENCE.equalsIgnoreCase(tableName))
        ) {
            statements.add("DELETE FROM " + tableName);
        }
    }
}