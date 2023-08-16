package migration.pk.lucidxpo.ynami.test;

import migration.pk.lucidxpo.ynami.helper.DBCleaner;
import migration.pk.lucidxpo.ynami.helper.DBMigrationCheck;
import migration.pk.lucidxpo.ynami.helper.MigrationScriptFetcher;
import migration.pk.lucidxpo.ynami.helper.MultiSqlExecutor;
import migration.pk.lucidxpo.ynami.helper.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.columnExists;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.constraintExistsFor;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.constraintExistsForTable;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.dataSourceForLocalMySql;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.executorForLocalMySql;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.hasColumnWith;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.tableExists;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBMigrationScriptTest {
    private static final boolean IS_NULLABLE = true;
    private static final boolean NOT_NULLABLE = false;
    private static final String DATA_TYPE_BIT = "BIT";
    private static final String DATA_TYPE_INTEGER = "INT";
    private static final String DATA_TYPE_BIGINT = "BIGINT";
    private static final String DATA_TYPE_VARCHAR = "VARCHAR";
    private static final String DATA_TYPE_LONGTEXT = "LONGTEXT";
    private static final String DATA_TYPE_TIMESTAMP = "TIMESTAMP";

    private DBMigrationCheck migrationCheck;

    @BeforeEach
    void setup() {
        final MultiSqlExecutor executor = executorForLocalMySql();
        final DBCleaner dbCleaner = new DBCleaner(executor);
        final MigrationScriptFetcher fetcher = new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH);
        final TransactionOperations transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSourceForLocalMySql()));

        migrationCheck = new DBMigrationCheck(dbCleaner, fetcher, executor, transaction);
    }

    @Test
    void shouldCreateBaselineSchemaTables() throws Exception {
        final Operation preOperation = executor -> {
            assertFalse(tableExists("hibernate_sequence", executor));
            assertFalse(tableExists("FeatureToggles", executor));
            assertFalse(tableExists("Users", executor));
            assertFalse(tableExists("Roles", executor));
            assertFalse(tableExists("UserRoles", executor));
        };

        final Operation postOperation = executor -> {
            assertTrue(tableExists("hibernate_sequence", executor));

            assertTrue(hasColumnWith(executor, "hibernate_sequence", "next_val", DATA_TYPE_BIGINT, IS_NULLABLE, 19));

            final List<Map<String, Object>> hibernateSequenceRows = executor.getRowExtractor("hibernate_sequence").getRowsWithSpecifiedField("next_val", "0");
            assertThat(hibernateSequenceRows.size(), is(1));

            assertTrue(tableExists("FeatureToggles", executor));
            assertTrue(constraintExistsForTable(executor, "FeatureToggles", "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsFor(executor, "FeatureToggles", "FEATURE_NAME", "PRI"));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "FEATURE_NAME", DATA_TYPE_VARCHAR, NOT_NULLABLE, 100));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "FEATURE_ENABLED", DATA_TYPE_INTEGER, IS_NULLABLE));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "STRATEGY_ID", DATA_TYPE_VARCHAR, IS_NULLABLE, 200));
            assertTrue(hasColumnWith(executor, "FeatureToggles", "STRATEGY_PARAMS", DATA_TYPE_VARCHAR, IS_NULLABLE, 2000));

            final String usersTableName = "Users";
            assertTrue(tableExists(usersTableName, executor));
            assertTrue(constraintExistsForTable(executor, usersTableName, "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsForTable(executor, usersTableName, "UNIQUE", "UK_USERS_EMAIL"));
            assertTrue(constraintExistsForTable(executor, usersTableName, "UNIQUE", "UK_USERS_USERNAME"));
            assertTrue(constraintExistsFor(executor, usersTableName, "id", "PRI"));
            assertTrue(constraintExistsFor(executor, usersTableName, "email", "UNI"));
            assertTrue(constraintExistsFor(executor, usersTableName, "username", "UNI"));
            assertTrue(hasColumnWith(executor, usersTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
            assertTrue(hasColumnWith(executor, usersTableName, "name", DATA_TYPE_VARCHAR, NOT_NULLABLE, 40));
            assertTrue(hasColumnWith(executor, usersTableName, "username", DATA_TYPE_VARCHAR, NOT_NULLABLE, 40));
            assertTrue(hasColumnWith(executor, usersTableName, "email", DATA_TYPE_VARCHAR, NOT_NULLABLE, 40));
            assertTrue(hasColumnWith(executor, usersTableName, "password", DATA_TYPE_VARCHAR, NOT_NULLABLE, 100));
            assertAuditColumns(executor, usersTableName);

            final String rolesTableName = "Roles";
            assertTrue(tableExists(rolesTableName, executor));
            assertTrue(constraintExistsForTable(executor, rolesTableName, "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsForTable(executor, rolesTableName, "UNIQUE", "UK_ROLES_NAME"));
            assertTrue(constraintExistsFor(executor, rolesTableName, "id", "PRI"));
            assertTrue(constraintExistsFor(executor, rolesTableName, "name", "UNI"));
            assertTrue(hasColumnWith(executor, rolesTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
            assertTrue(hasColumnWith(executor, rolesTableName, "name", DATA_TYPE_VARCHAR, NOT_NULLABLE, 60));
            assertAuditColumns(executor, rolesTableName);

            assertThat(executor.getRowExtractor(rolesTableName).getRowsWithSpecifiedField("name", "ROLE_USER").size(), is(1));
            assertThat(executor.getRowExtractor(rolesTableName).getRowsWithSpecifiedField("name", "ROLE_ADMIN").size(), is(1));
            assertThat(executor.getRowExtractor(rolesTableName).getRowsWithSpecifiedField("name", "ROLE_SUPPORT").size(), is(1));

            final String userRolesTableName = "UserRoles";
            assertTrue(tableExists(userRolesTableName, executor));
            assertTrue(constraintExistsForTable(executor, userRolesTableName, "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsForTable(executor, userRolesTableName, "FOREIGN KEY", "FK_USER_ROLES_ROLE_ID"));
            assertTrue(constraintExistsForTable(executor, userRolesTableName, "FOREIGN KEY", "FK_USER_ROLES_USER_ID"));
            assertTrue(hasColumnWith(executor, userRolesTableName, "userId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
            assertTrue(hasColumnWith(executor, userRolesTableName, "roleId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
        };

        migrationCheck.testDbMigrationWithScriptNumber(1, preOperation, postOperation);
    }

    @Test
    void shouldCreateSampleTable() throws Exception {
        final Operation preOperation = executor -> assertFalse(tableExists("Sample", executor));

        final Operation postOperation = executor -> {
            assertTrue(tableExists("Sample", executor));
            assertTrue(constraintExistsForTable(executor, "Sample", "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsFor(executor, "Sample", "id", "PRI"));
            assertTrue(hasColumnWith(executor, "Sample", "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
            assertTrue(hasColumnWith(executor, "Sample", "active", DATA_TYPE_BIT, IS_NULLABLE, 1));
            assertTrue(hasColumnWith(executor, "Sample", "address", DATA_TYPE_VARCHAR, IS_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, "Sample", "firstName", DATA_TYPE_VARCHAR, IS_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, "Sample", "lastName", DATA_TYPE_VARCHAR, IS_NULLABLE, 255));
        };

        migrationCheck.testDbMigrationWithScriptNumber(2, preOperation, postOperation);
    }

    @Test
    void shouldAddAuditColumnsToSampleTable() throws Exception {
        final Operation preOperation = executor -> {
            assertFalse(columnExists("Sample", "createdBy", executor));
            assertFalse(columnExists("Sample", "createdDate", executor));
            assertFalse(columnExists("Sample", "lastModifiedBy", executor));
            assertFalse(columnExists("Sample", "lastModifiedDate", executor));
        };

        final Operation postOperation = executor -> assertAuditColumns(executor, "Sample");

        migrationCheck.testDbMigrationWithScriptNumber(3, preOperation, postOperation);
    }

    @Test
    void shouldCreateAuditEntryAndAuditEntryArchiveTables() throws Exception {
        final String auditEntryTableName = "AuditEntry";
        final String auditEntryArchiveTableName = "AuditEntryArchive";
        final Operation preOperation = executor -> {
            assertFalse(tableExists(auditEntryTableName, executor));
            assertFalse(tableExists(auditEntryArchiveTableName, executor));
        };

        final Operation postOperation = executor -> {
            assertTrue(tableExists(auditEntryTableName, executor));
            assertTrue(constraintExistsForTable(executor, auditEntryTableName, "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsFor(executor, auditEntryTableName, "id", "PRI"));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "changedAt", DATA_TYPE_TIMESTAMP, NOT_NULLABLE));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "changedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "changedEntityId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "changedEntityName", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "fieldChanged", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "fromValue", DATA_TYPE_LONGTEXT, IS_NULLABLE));
            assertTrue(hasColumnWith(executor, auditEntryTableName, "toValue", DATA_TYPE_LONGTEXT, IS_NULLABLE));

            assertTrue(tableExists(auditEntryArchiveTableName, executor));
            assertTrue(constraintExistsForTable(executor, auditEntryArchiveTableName, "PRIMARY KEY", "PRIMARY"));
            assertTrue(constraintExistsFor(executor, auditEntryArchiveTableName, "id", "PRI"));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "changedAt", DATA_TYPE_TIMESTAMP, NOT_NULLABLE));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "changedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "changedEntityId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "changedEntityName", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "fieldChanged", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "fromValue", DATA_TYPE_LONGTEXT, IS_NULLABLE));
            assertTrue(hasColumnWith(executor, auditEntryArchiveTableName, "toValue", DATA_TYPE_LONGTEXT, IS_NULLABLE));
        };

        migrationCheck.testDbMigrationWithScriptNumber(4, preOperation, postOperation);
    }

    private void assertAuditColumns(MultiSqlExecutor executor, String usersTableName) {
        assertTrue(hasColumnWith(executor, usersTableName, "createdBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
        assertTrue(hasColumnWith(executor, usersTableName, "createdDate", DATA_TYPE_TIMESTAMP, NOT_NULLABLE));
        assertTrue(hasColumnWith(executor, usersTableName, "lastModifiedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255));
        assertTrue(hasColumnWith(executor, usersTableName, "lastModifiedDate", DATA_TYPE_TIMESTAMP, IS_NULLABLE));
    }
}
