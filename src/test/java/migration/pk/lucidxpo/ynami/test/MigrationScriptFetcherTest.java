package migration.pk.lucidxpo.ynami.test;

import migration.pk.lucidxpo.ynami.helper.MigrationScript;
import migration.pk.lucidxpo.ynami.helper.MigrationScriptFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.nio.charset.Charset.forName;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MigrationScriptFetcherTest implements BeforeEachCallback {
    private MigrationScriptFetcher fetcher;

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        fetcher = new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH);
    }

    @Test
    void shouldReturnAllMigrationScriptsBeforeACertainScriptNumber() throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(3);

        assertEquals(2, migrationScripts.size());

        assertThat(migrationScripts.get(0).getFileName(), startsWith("V001"));
        assertThat(migrationScripts.get(1).getFileName(), startsWith("V002"));

        assertEquals(contentForFile("V001__create_baseline.sql"), migrationScripts.get(0).getContent());
        assertEquals(contentForFile("V002__create_samples_table.sql"), migrationScripts.get(1).getContent());
    }

    @Test
    void shouldSkipIgnoredScriptNumber() throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(3, 2);

        assertEquals(1, migrationScripts.size());

        assertThat(migrationScripts.get(0).getFileName(), startsWith("V001"));
        assertEquals(contentForFile("V001__create_baseline.sql"), migrationScripts.get(0).getContent());
    }

    @Test
    void shouldAlwaysGetMigrationScriptInOrderBasedOnFileName() throws Exception {
        final List<MigrationScript> migrationScripts = fetcher.allMigrationScriptsBefore(3);

        for (int index = 0; index < 2; index++) {
            final MigrationScript script = migrationScripts.get(index);
            assertThat("Wrong order of file fetched", index + 1, equalTo(indexOf(script)));
        }
    }

    @Test
    void shouldReturnContentOfScriptWithCertainScriptNumber() throws Exception {
        final String content = fetcher.migrationScriptContentForIndex(2);

        assertEquals(contentForFile("V002__create_samples_table.sql"), content);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenTryingToFindScriptNumberThatDoesNotExists() {
        assertThrows(RuntimeException.class, () -> fetcher.migrationScriptContentForIndex(9999));
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
