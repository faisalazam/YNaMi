package pk.lucidxpo.ynami.migration.helper;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;
import static org.springframework.test.jdbc.JdbcTestUtils.dropTables;

public class DBCleaner {
    private final MultiSqlExecutor executor;

    public DBCleaner(final MultiSqlExecutor executor) {
        this.executor = executor;
    }

    public void cleanDB() throws Exception {
        final JdbcTemplate jdbcTemplate = (JdbcTemplate) executor.getTemplate();
        final List<String> tableNames = jdbcTemplate.queryForList("SHOW TABLES", String.class);
        dropTables(jdbcTemplate, tableNames.toArray(new String[]{}));
    }

    void cleanDBData() throws Exception {
        final JdbcTemplate jdbcTemplate = (JdbcTemplate) executor.getTemplate();
        final List<String> tableNames = jdbcTemplate.queryForList("SHOW TABLES", String.class);
        deleteFromTables(jdbcTemplate, tableNames.toArray(new String[]{}));
    }
}