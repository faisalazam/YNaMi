package migration.pk.lucidxpo.ynami.test;

import migration.pk.lucidxpo.ynami.helper.DBCleaner;
import migration.pk.lucidxpo.ynami.helper.DataPatchDBMigrationCheck;
import migration.pk.lucidxpo.ynami.helper.MultiSqlExecutor;
import migration.pk.lucidxpo.ynami.helper.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.dataSourceForLocalMySql;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.executorForLocalMySql;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static pk.lucidxpo.ynami.utils.Identity.randomInt;

class DBDataPatchScriptTest {
    private static final String SAMPLE_TABLE = "Sample";
    private static final String STATUS_COLUMN = "active";

    private DataPatchDBMigrationCheck dataPatchMigrationCheck;

    @BeforeEach
    void setup() {
        final MultiSqlExecutor executor = executorForLocalMySql();
        final DBCleaner dbCleaner = new DBCleaner(executor);
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSourceForLocalMySql());
        final TransactionOperations transaction = new TransactionTemplate(transactionManager);

        dataPatchMigrationCheck = new DataPatchDBMigrationCheck(dbCleaner, executor, transaction);
    }

    @Test
    void shouldVerifyThatSampleHasBeenActivated() throws Exception {
        final Integer id = randomInt();

        final Operation preOperation = executor -> new InsertSample()
                .withId(id)
                .withActive(0)
                .to(executor);

        final Operation postOperation = new Operation() {
            @Override
            public void execute(final MultiSqlExecutor executor) {
                verifySampleStatus(executor, id);
            }

            private void verifySampleStatus(final MultiSqlExecutor executor, final Integer sampleId) {
                final Map<String, Object> sampleRow = executor.getRowExtractor(SAMPLE_TABLE).getSingleRowWithId(Integer.toString(sampleId));
                assertThat(sampleRow.get(STATUS_COLUMN), is(true));
            }
        };

        dataPatchMigrationCheck.testDbMigrationWithScriptName("update_status_in_sample_table.sql", preOperation, postOperation, 3, false);
    }
}