package pk.lucidxpo.ynami.migration.test;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import pk.lucidxpo.ynami.migration.helper.DBCleaner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.jdbc.JdbcTestUtils.dropTables;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCHEMA_NAME;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.executorForLocalMySql;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.jdbcTemplateForLocalMySql;

class DBCleanerTest {

    private static final String TABLE_SELECTOR_QUERY = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "'";

    private JdbcTemplate jdbcTemplate;
    private DBCleaner dbCleaner;

    @BeforeEach
    void setup() {
        jdbcTemplate = jdbcTemplateForLocalMySql();
        dropTableIfExists();

        dbCleaner = new DBCleaner(executorForLocalMySql());
    }

    @Test
    void shouldExecuteDBCleanScript() {
        jdbcTemplate.execute("create table Sample (id varchar(255))");
        jdbcTemplate.execute("create table Sample2 (id varchar(255))");


        assertThat(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class), hasItem("Sample"));
        assertThat(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class), hasItem("Sample2"));

        dbCleaner.cleanDB();

        assertFalse(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class).contains("Sample"));
        assertFalse(jdbcTemplate.queryForList(TABLE_SELECTOR_QUERY, String.class).contains("Sample2"));
    }

    @Test
    void shouldDropTableWithConstraints() {
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
    void cleanDB() {
        dropTableIfExists();
    }

    private void dropTableIfExists() {
        try {
            dropTables(jdbcTemplate, "Sample", "Sample2");
        } catch (Exception e) {
            // that means the table already deleted, we don't care !!
        }
    }
}