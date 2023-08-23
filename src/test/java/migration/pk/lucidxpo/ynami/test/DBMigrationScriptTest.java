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

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.CONNECT_TO_MYSQL;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.assertColumnWith;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.assertConstraintExistsFor;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.assertConstraintExistsForTable;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.columnExists;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.executorForLocalMySql;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.localDataSource;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.tableExists;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DBMigrationScriptTest {
    public static final String DATA_TYPE_BIT = "BIT";
    public static final String DATA_TYPE_H2_BIT = "BOOLEAN";
    public static final String DATA_TYPE_INTEGER = "INT";
    public static final String DATA_TYPE_H2_INTEGER = "INTEGER";
    public static final String DATA_TYPE_LONGTEXT = "LONGTEXT";
    public static final String DATA_TYPE_VARCHAR = "VARCHAR";
    public static final String DATA_TYPE_H2_VARCHAR = "CHARACTER VARYING";

    private static final boolean IS_NULLABLE = true;
    private static final boolean NOT_NULLABLE = false;
    private static final String DATA_TYPE_BIGINT = "BIGINT";
    private static final String DATA_TYPE_TIMESTAMP = "TIMESTAMP";

    private DBMigrationCheck migrationCheck;

    @BeforeEach
    void setup() {
        final MultiSqlExecutor executor = executorForLocalMySql();
        final DBCleaner dbCleaner = new DBCleaner(executor);
        final DataSource dataSource = localDataSource();
        final MigrationScriptFetcher fetcher = new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH);
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        final TransactionOperations transaction = new TransactionTemplate(transactionManager);

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

            assertColumnWith(executor, "hibernate_sequence", "next_val",
                    DATA_TYPE_BIGINT, IS_NULLABLE, CONNECT_TO_MYSQL ? 19 : 64);

            final List<Map<String, Object>> hibernateSequenceRows = executor
                    .getRowExtractor("hibernate_sequence")
                    .getRowsWithSpecifiedField("next_val", "0");
            assertThat(hibernateSequenceRows.size(), is(1));

            final String featureTogglesTableName = "FeatureToggles";
            assertTrue(tableExists(featureTogglesTableName, executor));
            assertConstraintExistsForTable(executor, featureTogglesTableName, "PRIMARY KEY", "PK_FeatureToggles");
            assertConstraintExistsFor(executor, featureTogglesTableName, "FEATURE_NAME", "PRI");
            assertColumnWith(executor, featureTogglesTableName, "FEATURE_NAME", DATA_TYPE_VARCHAR, NOT_NULLABLE, 100);
            assertColumnWith(executor, featureTogglesTableName, "FEATURE_ENABLED", DATA_TYPE_INTEGER, IS_NULLABLE);
            assertColumnWith(executor, featureTogglesTableName, "STRATEGY_ID", DATA_TYPE_VARCHAR, IS_NULLABLE, 200);
            assertColumnWith(executor, featureTogglesTableName, "STRATEGY_PARAMS", DATA_TYPE_VARCHAR, IS_NULLABLE, 2000);

            final String usersTableName = "Users";
            assertTrue(tableExists(usersTableName, executor));
            assertConstraintExistsForTable(executor, usersTableName, "PRIMARY KEY", "PK_Users");
            assertConstraintExistsForTable(executor, usersTableName, "UNIQUE", "UK_USERS_EMAIL");
            assertConstraintExistsForTable(executor, usersTableName, "UNIQUE", "UK_USERS_USERNAME");
            assertConstraintExistsFor(executor, usersTableName, "id", "PRI");
            assertConstraintExistsFor(executor, usersTableName, "email", "UNI");
            assertConstraintExistsFor(executor, usersTableName, "username", "UNI");
            assertColumnWith(executor, usersTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
            assertColumnWith(executor, usersTableName, "name", DATA_TYPE_VARCHAR, NOT_NULLABLE, 40);
            assertColumnWith(executor, usersTableName, "username", DATA_TYPE_VARCHAR, NOT_NULLABLE, 40);
            assertColumnWith(executor, usersTableName, "email", DATA_TYPE_VARCHAR, NOT_NULLABLE, 40);
            assertColumnWith(executor, usersTableName, "password", DATA_TYPE_VARCHAR, NOT_NULLABLE, 100);
            assertAuditColumns(executor, usersTableName);

            final String rolesTableName = "Roles";
            assertTrue(tableExists(rolesTableName, executor));
            assertConstraintExistsForTable(executor, rolesTableName, "PRIMARY KEY", "PK_Roles");
            assertConstraintExistsForTable(executor, rolesTableName, "UNIQUE", "UK_ROLES_NAME");
            assertConstraintExistsFor(executor, rolesTableName, "id", "PRI");
            assertConstraintExistsFor(executor, rolesTableName, "name", "UNI");
            assertColumnWith(executor, rolesTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
            assertColumnWith(executor, rolesTableName, "name", DATA_TYPE_VARCHAR, NOT_NULLABLE, 60);
            assertAuditColumns(executor, rolesTableName);

            assertThat(executor.getRowExtractor(rolesTableName).getRowsWithSpecifiedField("name", "ROLE_USER").size(), is(1));
            assertThat(executor.getRowExtractor(rolesTableName).getRowsWithSpecifiedField("name", "ROLE_ADMIN").size(), is(1));
            assertThat(executor.getRowExtractor(rolesTableName).getRowsWithSpecifiedField("name", "ROLE_SUPPORT").size(), is(1));

            final String userRolesTableName = "UserRoles";
            assertTrue(tableExists(userRolesTableName, executor));
            assertConstraintExistsForTable(executor, userRolesTableName, "PRIMARY KEY", "PK_UserRoles");
            assertConstraintExistsForTable(executor, userRolesTableName, "FOREIGN KEY", "FK_USER_ROLES_ROLE_ID");
            assertConstraintExistsForTable(executor, userRolesTableName, "FOREIGN KEY", "FK_USER_ROLES_USER_ID");
            assertColumnWith(executor, userRolesTableName, "userId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
            assertColumnWith(executor, userRolesTableName, "roleId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
        };

        migrationCheck.testDbMigrationWithScriptNumber(1, preOperation, postOperation);
    }

    @Test
    void shouldCreateSampleTable() throws Exception {
        final Operation preOperation = executor -> assertFalse(tableExists("Sample", executor));

        final Operation postOperation = executor -> {
            assertTrue(tableExists("Sample", executor));
            assertConstraintExistsForTable(executor, "Sample", "PRIMARY KEY", "PK_Sample");
            assertConstraintExistsFor(executor, "Sample", "id", "PRI");
            assertColumnWith(executor, "Sample", "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
            assertColumnWith(executor, "Sample", "active", DATA_TYPE_BIT, IS_NULLABLE, 1);
            assertColumnWith(executor, "Sample", "address", DATA_TYPE_VARCHAR, IS_NULLABLE, 255);
            assertColumnWith(executor, "Sample", "firstName", DATA_TYPE_VARCHAR, IS_NULLABLE, 255);
            assertColumnWith(executor, "Sample", "lastName", DATA_TYPE_VARCHAR, IS_NULLABLE, 255);
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
            assertConstraintExistsForTable(executor, auditEntryTableName, "PRIMARY KEY", "PK_AuditEntry");
            assertConstraintExistsFor(executor, auditEntryTableName, "id", "PRI");
            assertColumnWith(executor, auditEntryTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
            assertColumnWith(executor, auditEntryTableName, "changedAt", DATA_TYPE_TIMESTAMP, NOT_NULLABLE, 6);
            assertColumnWith(executor, auditEntryTableName, "changedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryTableName, "changedEntityId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryTableName, "changedEntityName", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryTableName, "fieldChanged", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryTableName, "fromValue", DATA_TYPE_LONGTEXT, IS_NULLABLE);
            assertColumnWith(executor, auditEntryTableName, "toValue", DATA_TYPE_LONGTEXT, IS_NULLABLE);

            assertTrue(tableExists(auditEntryArchiveTableName, executor));
            assertConstraintExistsForTable(executor, auditEntryArchiveTableName, "PRIMARY KEY", "PK_AuditEntryArchive");
            assertConstraintExistsFor(executor, auditEntryArchiveTableName, "id", "PRI");
            assertColumnWith(executor, auditEntryArchiveTableName, "id", DATA_TYPE_VARCHAR, NOT_NULLABLE, 50);
            assertColumnWith(executor, auditEntryArchiveTableName, "changedAt", DATA_TYPE_TIMESTAMP, NOT_NULLABLE, 6);
            assertColumnWith(executor, auditEntryArchiveTableName, "changedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryArchiveTableName, "changedEntityId", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryArchiveTableName, "changedEntityName", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryArchiveTableName, "fieldChanged", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
            assertColumnWith(executor, auditEntryArchiveTableName, "fromValue", DATA_TYPE_LONGTEXT, IS_NULLABLE);
            assertColumnWith(executor, auditEntryArchiveTableName, "toValue", DATA_TYPE_LONGTEXT, IS_NULLABLE);
        };

        migrationCheck.testDbMigrationWithScriptNumber(4, preOperation, postOperation);
    }

    private void assertAuditColumns(MultiSqlExecutor executor, String usersTableName) {
        assertColumnWith(executor, usersTableName, "createdBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
        assertColumnWith(executor, usersTableName, "createdDate", DATA_TYPE_TIMESTAMP, NOT_NULLABLE, 6);
        assertColumnWith(executor, usersTableName, "lastModifiedBy", DATA_TYPE_VARCHAR, NOT_NULLABLE, 255);
        assertColumnWith(executor, usersTableName, "lastModifiedDate", DATA_TYPE_TIMESTAMP, IS_NULLABLE, 6);
    }
}
