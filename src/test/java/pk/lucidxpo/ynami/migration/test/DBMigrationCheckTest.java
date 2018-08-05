package pk.lucidxpo.ynami.migration.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import pk.lucidxpo.ynami.migration.helper.DBCleaner;
import pk.lucidxpo.ynami.migration.helper.DBMigrationCheck;
import pk.lucidxpo.ynami.migration.helper.MigrationScriptFetcher;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;
import pk.lucidxpo.ynami.migration.helper.Operation;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class DBMigrationCheckTest {

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

    @Before
    public void setUp() throws Exception {
        transaction = new StubTransaction();
        migrationCheck = new DBMigrationCheck(dbCleaner, fetcher, executor, transaction);
    }

    @Test
    public void shouldCleanDatabaseDoPrecheckExecuteScriptUnderTestDoPostCheckAndCleanTheDatabaseUnderTransaction() throws Exception {
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
    public void shouldExecuteTheCompleteScriptIncludingTheRollback() throws Exception {
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
    public void shouldExecuteTheCompleteScriptExcludingTheRollback() throws Exception {
        final String scriptContent = "some sql script --//@UNDO roll back script";

        given(fetcher.migrationScriptContentForIndex(eq(2))).willReturn(scriptContent);

        migrationCheck.testDbMigrationWithScriptNumber(2, preOperation, postOperation, false);

        verify(dbCleaner).cleanDB();
        verify(preOperation).execute(executor);
        verify(executor).execute(scriptContent);
        verify(postOperation).execute(executor);

        assertTrue(transaction.isExecuted());
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotFetchAndExecuteMigrationScriptWhenPrecheckThrowsException() throws Exception {
        final int scriptNumber = 2;

        doThrow(new RuntimeException()).when(preOperation).execute(executor);

        try {
            migrationCheck.testDbMigrationWithScriptNumber(scriptNumber, preOperation, postOperation);
        } finally {
            verifyZeroInteractions(executor);
            verifyZeroInteractions(postOperation);
        }
    }

    private class StubTransaction implements TransactionOperations {
        private boolean isExecuted;

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