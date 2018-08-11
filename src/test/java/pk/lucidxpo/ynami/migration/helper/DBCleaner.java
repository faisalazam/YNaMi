package pk.lucidxpo.ynami.migration.helper;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

public class DBCleaner {
    private final MultiSqlExecutor executor;

    public DBCleaner(final MultiSqlExecutor executor) {
        this.executor = executor;
    }

    public void cleanDB() {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(((JdbcTemplate) executor.getTemplate()).getDataSource());
        flyway.clean();
    }

    void cleanDBData() {
        final JdbcTemplate jdbcTemplate = (JdbcTemplate) executor.getTemplate();
        final List<String> tableNames = jdbcTemplate.queryForList("SHOW TABLES", String.class);
        deleteFromTables(jdbcTemplate, tableNames.toArray(new String[]{}));
    }
}