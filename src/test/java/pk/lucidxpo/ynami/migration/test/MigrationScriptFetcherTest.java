package pk.lucidxpo.ynami.migration.test;

import org.junit.Before;
import org.junit.Test;
import pk.lucidxpo.ynami.migration.helper.MigrationScript;
import pk.lucidxpo.ynami.migration.helper.MigrationScriptFetcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.nio.charset.Charset.forName;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;

public class MigrationScriptFetcherTest {
    private MigrationScriptFetcher fetcher;

    @Before
    public void setUp() throws Exception {
        fetcher = new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH);
    }

    @Test
    public void shouldReturnAllMigrationScriptsBeforeACertainScriptNumber() throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(3);

        assertThat(migrationScripts.size(), equalTo(2));

        assertThat(migrationScripts.get(0).getFileName(), startsWith("V001"));
        assertThat(migrationScripts.get(1).getFileName(), startsWith("V002"));

        assertThat(migrationScripts.get(0).getContent(), equalTo(contentForFile("V001__create_baseline.sql")));
        assertThat(migrationScripts.get(1).getContent(), equalTo(contentForFile("V002__create_samples_table.sql")));
    }

    @Test
    public void shouldSkipIgnoredScriptNumber() throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(3, 2);

        assertThat(migrationScripts.size(), equalTo(1));

        assertThat(migrationScripts.get(0).getFileName(), startsWith("V001"));
        assertThat(migrationScripts.get(0).getContent(), equalTo(contentForFile("V001__create_baseline.sql")));
    }

    @Test
    public void shouldAlwaysGetMigrationScriptInOrderBasedOnFileName() throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(3);

        for (int index = 0; index < 2; index++) {
            final MigrationScript script = migrationScripts.get(index);
            assertThat("Wrong order of file fetched", index + 1, equalTo(indexOf(script)));
        }
    }

    @Test
    public void shouldReturnContentOfScriptWithCertainScriptNumber() throws Exception {
        final String content = fetcher.migrationScriptContentForIndex(2);

        assertThat(content, equalTo(contentForFile("V002__create_samples_table.sql")));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenTryingToFindScriptNumberThatDoesNotExists() throws Exception {
        fetcher.migrationScriptContentForIndex(9999);
    }

    private String contentForFile(final String fileName) throws IOException {
        final String fullPath = SCRIPT_DIRECTORY_PATH + "/" + fileName;
        final File file = new File(fullPath);

        return readFileToString(file, forName("UTF8"));
    }

    private int indexOf(final MigrationScript migrationScript) {
        return parseInt(migrationScript.getFileName().substring(1, 4));
    }
}
