package migration.pk.lucidxpo.ynami.helper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;

public class MigrationTestHelper {
    // TODO Spring Upgrade: read value of isMySql from properties file somehow???
    public static final boolean CONNECT_TO_MYSQL = true;
    public static final String SCHEMA_NAME = "MigrationTestSchema";
    public static final String SCHEMA_NAME_PLACEHOLDER_REGEX = "\\$\\{schema_name}";
    public static final String PATCH_DIRECTORY_PATH = "src/main/resources/db/datapatch";
    public static final String SCRIPT_DIRECTORY_PATH = "src/main/resources/db/migration";

    private static final int[] SCRIPT_NUMBERS_TO_IGNORE = {};

    public static JdbcTemplate jdbcTemplateForLocalMySql() {
        return new JdbcTemplate(localDataSource());
    }

    public static DataSource localDataSource() {
        final String dataSourceUrl, userName, password;
        if (CONNECT_TO_MYSQL) {
            userName = "root";
            password = "root";
            dataSourceUrl = "jdbc:mysql://localhost:3306/" + SCHEMA_NAME +
                    "?createDatabaseIfNotExist=true" +
                    "&useSSL=false&allowPublicKeyRetrieval=true";
        } else {
            userName = "sa";
            password = EMPTY_STRING;
            dataSourceUrl = "jdbc:h2:mem:" + SCHEMA_NAME + ";" +
                    "MODE=MySQL;" +
                    "DB_CLOSE_DELAY=-1;" +
                    "DB_CLOSE_ON_EXIT=FALSE;" +
                    "DATABASE_TO_UPPER=false;" +
                    "INIT=CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME + "\\;" +
                    "SET SCHEMA " + SCHEMA_NAME + ";";
        }
        return new DriverManagerDataSource(dataSourceUrl, userName, password);
    }

    public static MultiSqlExecutor executorForLocalMySql() {
        return new MultiSqlExecutor(jdbcTemplateForLocalMySql());
    }

    public static void evolveDatabase(final MigrationScriptFetcher fetcher, final MultiSqlExecutor template) throws IOException {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScripts(SCRIPT_NUMBERS_TO_IGNORE);
        executeScripts(template, migrationScripts);
    }

    static void evolveDatabaseToPenultimatePoint(final int scriptNumber,
                                                 final MigrationScriptFetcher fetcher,
                                                 final MultiSqlExecutor template) throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(scriptNumber, SCRIPT_NUMBERS_TO_IGNORE);
        executeScripts(template, migrationScripts);
    }

    public static boolean tableExists(final String tableName, final MultiSqlExecutor executor) {
        final Integer count = executor.getTemplate().queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                        + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                , Integer.class
        );
        return count != null && count > 0;
    }

    public static boolean columnExists(final String tableName, final String columnName, final MultiSqlExecutor executor) {
        return hasColumnWith(executor, tableName, columnName);
    }

    public static boolean hasColumnWith(final MultiSqlExecutor executor, final String tableName, final String columnName) {
        final Integer count = executor.getTemplate().queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.COLUMNS "
                        + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                        + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                        + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' "
                , Integer.class
        );
        return count != null && count > 0;
    }

    public static boolean hasColumnWith(final MultiSqlExecutor executor,
                                        final String tableName,
                                        final String columnName,
                                        final String dataType,
                                        final boolean nullable) {
        final Integer count = executor.getTemplate().queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.COLUMNS "
                        + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                        + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                        + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' "
                        + "AND IS_NULLABLE = '" + (nullable ? "YES" : "NO") + "' "
                        + "AND upper(DATA_TYPE) = '" + dataType.toUpperCase() + "' "
                , Integer.class
        );
        return count != null && count > 0;
    }

    public static boolean hasColumnWith(final MultiSqlExecutor executor,
                                        final String tableName,
                                        final String columnName,
                                        final String dataType,
                                        final boolean nullable,
                                        final int size) {

        final Integer count = executor.getTemplate().queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.COLUMNS "
                        + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                        + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                        + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' "
                        + "AND IS_NULLABLE = '" + (nullable ? "YES" : "NO") + "' "
                        + "AND upper(DATA_TYPE) = '" + dataType.toUpperCase() + "' "
                        + "AND " + getSizeColumnName(dataType) + " = '" + size + "'"
                , Integer.class
        );
        return count != null && count > 0;
    }

    public static String getSizeColumnName(final String dataType) {
        if (dataType.toLowerCase().contains("int") || dataType.toLowerCase().contains("bit")) {
            return "NUMERIC_PRECISION";
        } else if (dataType.toLowerCase().contains("timestamp")) {
            return "DATETIME_PRECISION";
        } else {
            return "CHARACTER_MAXIMUM_LENGTH";
        }
    }

    public static boolean constraintExistsForTable(final MultiSqlExecutor executor,
                                                   final String tableName,
                                                   final String constrainType,
                                                   final String constraintName) {
        final Integer count = executor.getTemplate().queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS "
                        + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                        + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                        + "AND upper(CONSTRAINT_TYPE) = '" + constrainType.toUpperCase() + "' "
                        + "AND upper(CONSTRAINT_NAME) = '" + constraintName.toUpperCase() + "' "
                , Integer.class
        );
        return count != null && count > 0;
    }

    public static boolean constraintExistsFor(final MultiSqlExecutor executor,
                                              final String tableName,
                                              final String columnName,
                                              final String columnKey) {
        final Integer count = executor.getTemplate().queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.COLUMNS "
                        + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                        + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                        + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' "
                        + "AND COLUMN_KEY = '" + columnKey.toUpperCase() + "' "
                , Integer.class
        );
        return count != null && count > 0;
    }

    private static void executeScripts(final MultiSqlExecutor template, final List<MigrationScript> migrationScripts) {
        for (final MigrationScript migrationScript : migrationScripts) {
            System.out.println("==============  Executing migration script " + migrationScript.getFileName() + " ============");
            final String sqlQuery = migrationScript.getContent().replaceAll(SCHEMA_NAME_PLACEHOLDER_REGEX, SCHEMA_NAME);
            template.execute(sqlQuery);
            System.out.println("==============  Finished Executing migration script " + migrationScript.getFileName() + " ============");
        }
    }
}