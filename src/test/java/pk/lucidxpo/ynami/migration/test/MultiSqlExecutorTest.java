package pk.lucidxpo.ynami.migration.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MultiSqlExecutorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private MultiSqlExecutor executor;

    @Before
    public void setUp() throws Exception {
        executor = new MultiSqlExecutor(jdbcTemplate);
    }

    @Test
    public void shouldSplitSqlStatementBySemicolonAndExecute() throws Exception {
        executor.execute("sql1;sql2;sql3;sql4,blah;sql5(blah)");

        verify(jdbcTemplate).execute("sql1");
        verify(jdbcTemplate).execute("sql2");
        verify(jdbcTemplate).execute("sql3");
        verify(jdbcTemplate).execute("sql4,blah");
        verify(jdbcTemplate).execute("sql5(blah)");
    }

    @Test
    public void shouldNotExecuteEmptySqlStatement() throws Exception {
        executor.execute("sql1;   ;sql2;;sql3;\n\n");

        verify(jdbcTemplate, new Times(3)).execute(any(String.class));

        verify(jdbcTemplate).execute("sql1");
        verify(jdbcTemplate).execute("sql2");
        verify(jdbcTemplate).execute("sql3");
    }

    @Test
    public void shouldNotExecuteAnySqlStatementAfterUndoComment() throws Exception {
        executor.execute("sql1;sql2;sql3;--//@UNDO sql4;sql5\n\n");

        verify(jdbcTemplate, new Times(3)).execute(any(String.class));

        verify(jdbcTemplate).execute("sql1");
        verify(jdbcTemplate).execute("sql2");
        verify(jdbcTemplate).execute("sql3");
    }
}
