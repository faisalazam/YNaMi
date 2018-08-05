package pk.lucidxpo.ynami.migration.helper;

import java.util.List;

public class DBCleaner {
    private final MultiSqlExecutor executor;

    public DBCleaner(final MultiSqlExecutor executor) {
        this.executor = executor;
    }

    public void cleanDB() throws Exception {
        final List<String> tableNames = executor.getTemplate().queryForList("SHOW TABLES", String.class);

        executor.execute("SET FOREIGN_KEY_CHECKS = 0;");
        for (final String tableName : tableNames) {
            executor.execute("DROP TABLE IF EXISTS " + tableName);
        }
        executor.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }

    void cleanDBData() throws Exception {
        final List<String> tableNames = executor.getTemplate().queryForList("SHOW TABLES", String.class);

        for (final String tableName : tableNames) {
            executor.execute("DELETE FROM " + tableName);
        }
    }
}