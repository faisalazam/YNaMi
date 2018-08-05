package pk.lucidxpo.ynami.migration.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import pk.lucidxpo.ynami.migration.helper.DBCleaner;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertFalse;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCHEMA_NAME;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.executorForLocalMySql;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.jdbcTemplateForLocalMySql;

public class DBCleanerTest {

    private static final String TABLE_SELECTOR_QUERY = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "'";

    private JdbcTemplate jdbcTemplate;
    private MultiSqlExecutor executor;
    private DBCleaner dbCleaner;

    @Before
    public void setup() throws Exception {
        jdbcTemplate = jdbcTemplateForLocalMySql();
        executor = executorForLocalMySql();
        dropTableIfExists();

        dbCleaner = new DBCleaner(executor);
    }

    @Test
    public void shouldExecuteDBCleanScript() throws Exception {
        jdbcTemplate.execute("create table Sample (id varchar(255))");
        jdbcTemplate.execute("create table Sample2 (id varchar(255))");


        assertThat(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class), hasItem("Sample"));
        assertThat(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class), hasItem("Sample2"));

        dbCleaner.cleanDB();

        assertFalse(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class).contains("Sample"));
        assertFalse(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class).contains("Sample2"));
    }

    @Test
    public void shouldDropTableWithConstraints() throws Exception {
        jdbcTemplate.execute("create table Sample (id varchar(255), id2 varchar(255) UNIQUE)");
        jdbcTemplate.execute("create table Sample2 (id varchar(255), primary key (id))");
        jdbcTemplate.execute("alter table Sample add constraint some_constraint foreign key (id2) references Sample2(id)");

        assertThat(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class), hasItem("Sample"));
        assertThat(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class), hasItem("Sample2"));

        dbCleaner.cleanDB();

        assertFalse(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class).contains("Sample"));
        assertFalse(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class).contains("Sample2"));
    }

    @After
    public void cleanDB() {
        dropTableIfExists();
    }

    private void dropTableIfExists() {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("DROP TABLE Sample");
            jdbcTemplate.execute("DROP TABLE Sample2");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (Exception e) {
            // that means the table already deleted, we don't care !!
        }
    }
}