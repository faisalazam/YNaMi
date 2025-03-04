package migration.pk.lucidxpo.ynami.helper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_BIT;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_H2_BIT;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_H2_INTEGER;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_H2_VARCHAR;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_INTEGER;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_LONGTEXT;
import static migration.pk.lucidxpo.ynami.test.DBMigrationScriptTest.DATA_TYPE_VARCHAR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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
            dataSourceUrl = "jdbc:h2:file:./h2_db/" + SCHEMA_NAME +
                    ";MODE=MySQL" +
                    ";AUTO_SERVER=TRUE" +
                    ";DB_CLOSE_DELAY=-1" +
                    ";DB_CLOSE_ON_EXIT=TRUE" +
                    ";DATABASE_TO_UPPER=FALSE" +
                    ";INIT=CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME + "\\" +
                    ";SET SCHEMA " + SCHEMA_NAME;
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
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(
                scriptNumber,
                SCRIPT_NUMBERS_TO_IGNORE
        );
        executeScripts(template, migrationScripts);
    }

    public static boolean tableExists(final String tableName, final MultiSqlExecutor executor) {
        final String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' ";
        System.out.println("\nCheck if table exists: \n" + query);

        final Integer count = executor.getTemplate().queryForObject(query, Integer.class);
        return count != null && count == 1;
    }

    public static boolean columnExists(final String tableName, final String columnName, final MultiSqlExecutor executor) {
        final String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' ";
        System.out.println("\nCheck if column exists with specifics: \n" + query);

        final Integer count = executor.getTemplate().queryForObject(query, Integer.class);
        return count != null && count == 1;
    }

    public static void assertColumnWith(final MultiSqlExecutor executor,
                                        final String tableName,
                                        final String columnName,
                                        final String dataType,
                                        final boolean nullable) {
        final String query = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' ";
        System.out.println("\nCheck if column exists with specifics: \n" + query);

        final Map<String, Object> result = executor.getTemplate().queryForMap(query);
        assertFalse(result.isEmpty());
        assertEquals((nullable ? "YES" : "NO"), result.get("IS_NULLABLE"));
        assertThat(result.get("DATA_TYPE").toString(), equalToIgnoringCase(getConvertedDataType(dataType)));
    }

    public static void assertColumnWith(final MultiSqlExecutor executor,
                                        final String tableName,
                                        final String columnName,
                                        final String dataType,
                                        final boolean nullable,
                                        final long size) {

        String query = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' ";
        System.out.println("\nCheck if column exists with specifics: \n" + query);

        final Map<String, Object> result = executor.getTemplate().queryForMap(query);
        assertFalse(result.isEmpty());
        assertEquals((nullable ? "YES" : "NO"), result.get("IS_NULLABLE"));

        final String convertedDataType = getConvertedDataType(dataType);
        assertThat(result.get("DATA_TYPE").toString(), equalToIgnoringCase(convertedDataType));

        final String sizeColumnName = getSizeColumnName(dataType);
        final Object sizeObject = result.get(sizeColumnName);
        if (sizeObject instanceof Long) {
            assertEquals(size, sizeObject);
        } else {
            if (DATA_TYPE_H2_BIT.equalsIgnoreCase(convertedDataType)) {
                // H2 has no size for this type
                assertNull(sizeObject);
            } else {
                // this is just for H2 compatibility as MySql will return the size in long always
                // but H2 will return the size in integer for DateTime columns and in long otherwise.
                assertEquals(((Long) size).intValue(), sizeObject);
            }
        }
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

    public static void assertConstraintExistsForTable(final MultiSqlExecutor executor,
                                                      final String tableName,
                                                      final String constraintType,
                                                      final String constraintName) {
        final String query = "SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' ";
        System.out.println("\nCheck if table exists with constraint: \n" + query);

        final List<Map<String, Object>> constraints = executor.getTemplate().queryForList(query);
        final String convertedConstraintName = getConvertedConstraintName(constraintType, constraintName);
        final long count = constraints
                .stream()
                .filter(map ->
                        map.get("CONSTRAINT_TYPE").toString().equalsIgnoreCase(constraintType) &&
                                map.get("CONSTRAINT_NAME").toString().equalsIgnoreCase(convertedConstraintName)
                ).count();
        assertEquals(1, count,
                "Found " + constraints.size() + " but none of them matched with specified attributes");
    }

    public static void assertConstraintExistsFor(final MultiSqlExecutor executor,
                                                 final String tableName,
                                                 final String columnName,
                                                 final String columnKey) {
        final String query = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' "
                + "AND upper(COLUMN_NAME) = '" + columnName.toUpperCase() + "' ";
        System.out.println("\nCheck if column exists with constraint: \n" + query);

        final Map<String, Object> result = executor.getTemplate().queryForMap(query);
        assertFalse(result.isEmpty());
        if (CONNECT_TO_MYSQL) { // h2 issue -> COLUMN_KEY doesn't exist in current (2.2.220) H2 database
            assertEquals(columnKey.toUpperCase(), result.get("COLUMN_KEY"));
        }
    }

    public static String getConvertedDataType(final String dataType) {
        if (CONNECT_TO_MYSQL) {
            return dataType;
        }
        if (DATA_TYPE_VARCHAR.equalsIgnoreCase(dataType) || DATA_TYPE_LONGTEXT.equalsIgnoreCase(dataType)) {
            return DATA_TYPE_H2_VARCHAR;
        } else if (DATA_TYPE_INTEGER.equalsIgnoreCase(dataType)) {
            return DATA_TYPE_H2_INTEGER;
        } else if (DATA_TYPE_BIT.equalsIgnoreCase(dataType)) {
            return DATA_TYPE_H2_BIT;
        }
        return dataType;
    }

    private static void executeScripts(final MultiSqlExecutor template, final List<MigrationScript> migrationScripts) {
        for (final MigrationScript migrationScript : migrationScripts) {
            System.out.println("==============  Executing migration script " + migrationScript.getFileName() + " ============");
            final String sqlQuery = migrationScript.getContent().replaceAll(SCHEMA_NAME_PLACEHOLDER_REGEX, SCHEMA_NAME);
            template.execute(sqlQuery);
            System.out.println("==============  Finished Executing migration script " + migrationScript.getFileName() + " ============");
        }
    }

    private static String getConvertedConstraintName(final String constraintType, final String constraintName) {
        return CONNECT_TO_MYSQL && "PRIMARY KEY".equals(constraintType) ? "PRIMARY" : constraintName;
    }
}