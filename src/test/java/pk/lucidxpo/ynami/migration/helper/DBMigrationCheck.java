package pk.lucidxpo.ynami.migration.helper;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;

import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.evolveDatabaseToPenultimatePoint;

public class DBMigrationCheck {

    private final DBCleaner dbCleaner;
    private final MultiSqlExecutor executor;
    private final MigrationScriptFetcher fetcher;
    private final TransactionOperations transaction;

    public DBMigrationCheck(final DBCleaner dbCleaner,
                            final MigrationScriptFetcher fetcher,
                            final MultiSqlExecutor executor,
                            final TransactionOperations transaction) {
        this.dbCleaner = dbCleaner;
        this.fetcher = fetcher;
        this.executor = executor;
        this.transaction = transaction;
    }

    public void testDbMigrationWithScriptNumber(final int migrationScriptNumber,
                                                final Operation preOperation,
                                                final Operation postOperation) throws Exception {
        testDbMigrationWithScriptNumber(migrationScriptNumber, preOperation, postOperation, false);
    }

    public void testDbMigrationWithScriptNumber(final int migrationScriptNumber,
                                                final Operation preOperation,
                                                final Operation postOperation,
                                                final boolean includeRollbackScript) throws Exception {
        transaction.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                try {
                    dbCleaner.cleanDB();
                    evolveDatabaseToPenultimatePoint(migrationScriptNumber, fetcher, executor);
                    preOperation.execute(executor);

                    String sqlQuery = fetcher.migrationScriptContentForIndex(migrationScriptNumber);
                    if (includeRollbackScript) {
                        sqlQuery = sqlQuery.replace("--//@UNDO", "");
                    }

                    executor.execute(sqlQuery);
                    postOperation.execute(executor);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}