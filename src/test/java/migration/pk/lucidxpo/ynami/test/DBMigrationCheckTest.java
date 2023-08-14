package migration.pk.lucidxpo.ynami.test;

import migration.pk.lucidxpo.ynami.helper.DBCleaner;
import migration.pk.lucidxpo.ynami.helper.DBMigrationCheck;
import migration.pk.lucidxpo.ynami.helper.MigrationScriptFetcher;
import migration.pk.lucidxpo.ynami.helper.MultiSqlExecutor;
import migration.pk.lucidxpo.ynami.helper.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(MockitoExtension.class)
class DBMigrationCheckTest implements BeforeEachCallback {

    @Mock
    private DBCleaner dbCleaner;
    @Mock
    private Operation preOperation;
    @Mock
    private Operation postOperation;
    @Mock
    private MultiSqlExecutor executor;
    @Mock
    private MigrationScriptFetcher fetcher;

    private StubTransaction transaction;
    private DBMigrationCheck migrationCheck;

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        transaction = new StubTransaction();
        migrationCheck = new DBMigrationCheck(dbCleaner, fetcher, executor, transaction);
    }

    @Test
    void shouldCleanDatabaseDoPrecheckExecuteScriptUnderTestDoPostCheckAndCleanTheDatabaseUnderTransaction() throws Exception {
        final String scriptContent = "some sql script";

        given(fetcher.migrationScriptContentForIndex(eq(2))).willReturn(scriptContent);

        migrationCheck.testDbMigrationWithScriptNumber(2, preOperation, postOperation);

        verify(dbCleaner).cleanDB();
        verify(preOperation).execute(executor);
        verify(executor).execute(scriptContent);
        verify(postOperation).execute(executor);

        assertTrue(transaction.isExecuted());
    }

    @Test
    void shouldExecuteTheCompleteScriptIncludingTheRollback() throws Exception {
        final String scriptContent = "some sql script --//@UNDO roll back script";
        final String scriptContentsToExecute = "some sql script  roll back script";

        given(fetcher.migrationScriptContentForIndex(eq(2))).willReturn(scriptContent);

        migrationCheck.testDbMigrationWithScriptNumber(2, preOperation, postOperation, true);

        verify(dbCleaner).cleanDB();
        verify(preOperation).execute(executor);
        verify(executor).execute(scriptContentsToExecute);
        verify(postOperation).execute(executor);

        assertTrue(transaction.isExecuted());
    }

    @Test
    void shouldExecuteTheCompleteScriptExcludingTheRollback() throws Exception {
        final String scriptContent = "some sql script --//@UNDO roll back script";

        given(fetcher.migrationScriptContentForIndex(eq(2))).willReturn(scriptContent);

        migrationCheck.testDbMigrationWithScriptNumber(2, preOperation, postOperation, false);

        verify(dbCleaner).cleanDB();
        verify(preOperation).execute(executor);
        verify(executor).execute(scriptContent);
        verify(postOperation).execute(executor);

        assertTrue(transaction.isExecuted());
    }

    @Test
    void shouldNotFetchAndExecuteMigrationScriptWhenPrecheckThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            final int scriptNumber = 2;

            doThrow(new RuntimeException()).when(preOperation).execute(executor);

            try {
                migrationCheck.testDbMigrationWithScriptNumber(scriptNumber, preOperation, postOperation);
            } finally {
                verifyZeroInteractions(executor);
                verifyZeroInteractions(postOperation);
            }
        });
    }

    private class StubTransaction implements TransactionOperations {
        private boolean isExecuted;

        @SuppressWarnings({"NullableProblems", "ConstantConditions"})
        @Override
        public <T> T execute(final TransactionCallback<T> callback) throws TransactionException {
            isExecuted = true;
            return callback.doInTransaction(null);
        }

        boolean isExecuted() {
            return isExecuted;
        }
    }
}