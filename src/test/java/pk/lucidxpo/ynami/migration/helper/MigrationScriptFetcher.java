package pk.lucidxpo.ynami.migration.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.nio.charset.Charset.forName;
import static java.util.Arrays.sort;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.springframework.util.CollectionUtils.arrayToList;

public class MigrationScriptFetcher {

    private File[] files = new File[0];

    public MigrationScriptFetcher(final String scriptDirectoryPath) {
        final File scriptDirectory = new File(scriptDirectoryPath);
        files = scriptDirectory.listFiles(new NoChecksumFileFilter());
        sort(files, new FileNameComparator());
    }

    public String migrationScriptContentForIndex(final int scriptNumber, final int... scriptNumbersToIgnore) throws IOException {
        final List<Integer> ignoredScriptsList = arrayToList(scriptNumbersToIgnore);
        for (final File file : files) {
            final int index = indexOf(file);
            if (index == scriptNumber && !ignoredScriptsList.contains(index)) {
                return readFileToString(file, "UTF-8");
            }
        }
        throw new RuntimeException("Can't find migration script for script number " + scriptNumber);
    }

    String migrationScriptContentForName(final String scriptName) throws IOException {
        for (final File file : files) {
            if (scriptName.equals(file.getName())) {
                return readFileToString(file, "UTF-8");
            }
        }
        throw new RuntimeException("Can't find migration script for script name " + scriptName);
    }

    public List<MigrationScript> allMigrationScriptsBefore(final int scriptNumber, final int... scriptNumbersToIgnore) throws IOException {
        final List<MigrationScript> migrationScripts = new ArrayList<>();
        final List<Integer> ignoredScriptsList = arrayToList(scriptNumbersToIgnore);

        for (final File file : files) {
            final int index = indexOf(file);

            if (index < scriptNumber && !ignoredScriptsList.contains(index)) {
                migrationScripts.add(new MigrationScript(file.getName(), readFileToString(file, forName("UTF8"))));
            } else if (ignoredScriptsList.contains(index)) {
                System.out.println("script index ignored = " + index);
            }

        }
        return migrationScripts;
    }

    private int indexOf(final File file) {
        return parseInt(file.getName().substring(1, 4));
    }

    private class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(final File file, final File otherFile) {
            return file.getName().compareTo(otherFile.getName());
        }
    }

    private class NoChecksumFileFilter implements FileFilter {
        @Override
        public boolean accept(final File file) {
            return !file.getName().startsWith("checksums.txt");
        }
    }
}