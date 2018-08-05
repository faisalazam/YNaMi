package pk.lucidxpo.ynami.migration.helper;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;

import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.PATCH_DIRECTORY_PATH;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.evolveDatabaseToPenultimatePoint;
import static pk.lucidxpo.ynami.utils.Identity.randomInt;

public class DataPatchDBMigrationCheck {
    private final DBCleaner dbCleaner;
    private final MultiSqlExecutor executor;
    private final TransactionOperations transaction;
    private final MigrationScriptFetcher deltasMigrationScriptFetcher;
    private final MigrationScriptFetcher dataPatchMigrationScriptFetcher;

    public DataPatchDBMigrationCheck(final DBCleaner dbCleaner,
                                     final MultiSqlExecutor executor,
                                     final TransactionOperations transaction) {
        this.dbCleaner = dbCleaner;
        this.executor = executor;
        this.transaction = transaction;
        this.deltasMigrationScriptFetcher = new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH);
        this.dataPatchMigrationScriptFetcher = new MigrationScriptFetcher(PATCH_DIRECTORY_PATH);
    }

    public void testDbMigrationWithScriptName(final String migrationScriptName,
                                              final Operation preOperation,
                                              final Operation postOperation,
                                              final int evolveDatabaseUpToScriptNumber,
                                              final boolean executeScriptMultipleTimes) throws Exception {
        transaction.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                try {
                    dbCleaner.cleanDB();
                    evolveDatabaseToPenultimatePoint(evolveDatabaseUpToScriptNumber, deltasMigrationScriptFetcher, executor);

                    dbCleaner.cleanDBData();
                    preOperation.execute(executor);

                    final int timesToRun = executeScriptMultipleTimes ? randomInt(2, 5) : 1;
                    for (int i = 0; i < timesToRun; i++) {
                        executor.execute(dataPatchMigrationScriptFetcher.migrationScriptContentForName(migrationScriptName));
                    }

                    postOperation.execute(executor);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
