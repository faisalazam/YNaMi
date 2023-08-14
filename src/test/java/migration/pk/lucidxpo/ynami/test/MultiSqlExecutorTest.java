package migration.pk.lucidxpo.ynami.test;

import migration.pk.lucidxpo.ynami.helper.MultiSqlExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MultiSqlExecutorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private MultiSqlExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new MultiSqlExecutor(jdbcTemplate);
    }

    @Test
    void shouldSplitSqlStatementBySemicolonAndExecute() {
        executor.execute("sql1;sql2;sql3;sql4,blah;sql5(blah)");

        verify(jdbcTemplate).execute("sql1");
        verify(jdbcTemplate).execute("sql2");
        verify(jdbcTemplate).execute("sql3");
        verify(jdbcTemplate).execute("sql4,blah");
        verify(jdbcTemplate).execute("sql5(blah)");
    }

    @Test
    void shouldNotExecuteEmptySqlStatement() {
        executor.execute("sql1;   ;sql2;;sql3;\n\n");

        verify(jdbcTemplate, new Times(3)).execute(any(String.class));

        verify(jdbcTemplate).execute("sql1");
        verify(jdbcTemplate).execute("sql2");
        verify(jdbcTemplate).execute("sql3");
    }

    @Test
    void shouldNotExecuteAnySqlStatementAfterUndoComment() {
        executor.execute("sql1;sql2;sql3;--//@UNDO sql4;sql5\n\n");

        verify(jdbcTemplate, new Times(3)).execute(any(String.class));

        verify(jdbcTemplate).execute("sql1");
        verify(jdbcTemplate).execute("sql2");
        verify(jdbcTemplate).execute("sql3");
    }
}
