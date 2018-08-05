package pk.lucidxpo.ynami.migration.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import pk.lucidxpo.ynami.migration.helper.DBCleaner;
import pk.lucidxpo.ynami.migration.helper.DataPatchDBMigrationCheck;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;
import pk.lucidxpo.ynami.migration.helper.Operation;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.dataSourceForLocalMySql;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.executorForLocalMySql;
import static pk.lucidxpo.ynami.utils.Identity.randomInt;

public class DBDataPatchScriptTest {
    private static final String SAMPLE_TABLE = "Sample";
    private static final String STATUS_COLUMN = "active";

    private DataPatchDBMigrationCheck dataPatchMigrationCheck;

    @Before
    public void setup() {
        final MultiSqlExecutor executor = executorForLocalMySql();
        final DBCleaner dbCleaner = new DBCleaner(executor);
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSourceForLocalMySql());
        final TransactionOperations transaction = new TransactionTemplate(transactionManager);

        dataPatchMigrationCheck = new DataPatchDBMigrationCheck(dbCleaner, executor, transaction);
    }

    @Test
    public void shouldVerifyThatSampleHasBeenActivated() throws Exception {
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