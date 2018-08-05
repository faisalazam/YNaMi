package pk.lucidxpo.ynami.migration.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import pk.lucidxpo.ynami.migration.helper.DBCleaner;
import pk.lucidxpo.ynami.migration.helper.DBMigrationCheck;
import pk.lucidxpo.ynami.migration.helper.MigrationScriptFetcher;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;
import pk.lucidxpo.ynami.migration.helper.Operation;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.columnExists;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.constraintExistsFor;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.dataSourceForLocalMySql;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.executorForLocalMySql;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.hasColumnWith;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.tableExists;

public class DBMigrationScriptTest {
    private static final boolean IS_NULLABLE = true;
    private static final boolean NOT_NULLABLE = false;
    private static final String DATA_TYPE_BIT = "BIT";
    private static final String DATA_TYPE_INTEGER = "INT";
    private static final String DATA_TYPE_BIGINT = "BIGINT";
    private static final String DATA_TYPE_VARCHAR = "VARCHAR";
    private static final String DATA_TYPE_TIMESTAMP = "TIMESTAMP";

    private DBMigrationCheck migrationCheck;

    @Before
    public void setup() {
        final MultiSqlExecutor executor = executorForLocalMySql();
        final DBCleaner dbCleaner = new DBCleaner(executor);
        final MigrationScriptFetcher fetcher = new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH);
        final TransactionOperations transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSourceForLocalMySql()));

        migrationCheck = new DBMigrationCheck(dbCleaner, fetcher, executor, transaction);
    }

    @Test
    public void shouldCreateHibernateSequenceAndFeatureTogglesTables() throws Exception {
        final Operation preOperation = executor -> {
            assertFalse(tableExists("hibernate_sequence", executor));
            assertFalse(tableExists("FeatureToggles", executor));
        };

        final Operation postOperation = executor -> {
            assertTrue(tableExists("hibernate_sequence", executor));

            assertTrue(hasColumnWith(executor, "hibernate_sequence", "next_val", DATA_TYPE_BIGINT, IS_NULLABLE, 19));

            final List<Map<String, Object>> hibernateSequenceRows = executor.getRowExtractor("hibernate_sequence").getRowsWithSpecifiedField("next_val", "0");
            assertThat(hibernateSequenceRows.size(), is(1));

            assertTrue(tableExists("FeatureToggles", executor));
            assertTrue(constraintExistsFor(executor, "FeatureToggles", "PRIMARY KEY"));
            assertTrue(constraintExistsFor(executor, "FeatureToggles", "FEATURE_NAME", "PRI"));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "FEATURE_NAME", DATA_TYPE_VARCHAR, NOT_NULLABLE, 100));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "FEATURE_ENABLED", DATA_TYPE_INTEGER, IS_NULLABLE));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "STRATEGY_ID", DATA_TYPE_VARCHAR, IS_NULLABLE, 200));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "STRATEGY_PARAMS", DATA_TYPE_VARCHAR, IS_NULLABLE, 2000));
        };

        migrationCheck.testDbMigrationWithScriptNumber(1, preOperation, postOperation);
    }

    @Test
    public void shouldCreateSampleTable() throws Exception {
        final Operation preOperation = executor -> assertFalse(tableExists("Sample", executor));

        final Operation postOperation = executor -> {
            assertTrue(tableExists("Sample", executor));
            assertTrue(constraintExistsFor(executor, "Sample", "PRIMARY KEY"));
            assertTrue(constraintExistsFor(executor, "Sample", "id", "PRI"));
            assertTrue(hasColumnWith(executor, "Sample", "id", DATA_TYPE_BIGINT, NOT_NULLABLE, 19));
            assertTrue(hasColumnWith(executor, "Sample", "active", DATA_TYPE_BIT, IS_NULLABLE, 1));
            assertTrue(hasColumnWith(executor, "Sample", "address", DATA_TYPE_VARCHAR, IS_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, "Sample", "firstName", DATA_TYPE_VARCHAR, IS_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, "Sample", "lastName", DATA_TYPE_VARCHAR, IS_NULLABLE, 255));
        };

        migrationCheck.testDbMigrationWithScriptNumber(2, preOperation, postOperation);
    }

    @Test
    public void shouldAddAuditColumnsToSampleTable() throws Exception {
        final Operation preOperation = executor -> {
            assertFalse(columnExists("Sample", "createdBy", executor));
            assertFalse(columnExists("Sample", "createdDate", executor));
            assertFalse(columnExists("Sample", "lastModifiedBy", executor));
            assertFalse(columnExists("Sample", "lastModifiedDate", executor));
        };

        final Operation postOperation = executor -> {
            assertTrue(hasColumnWith(executor, "Sample", "createdBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, "Sample", "createdDate", DATA_TYPE_TIMESTAMP, NOT_NULLABLE, 6));
            assertTrue(hasColumnWith(executor, "Sample", "lastModifiedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, "Sample", "lastModifiedDate", DATA_TYPE_TIMESTAMP, IS_NULLABLE, 6));
        };

        migrationCheck.testDbMigrationWithScriptNumber(3, preOperation, postOperation);
    }
}
