package pk.lucidxpo.ynami.migration.test;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;
import static org.junit.Assert.fail;
import static org.springframework.util.DigestUtils.md5DigestAsHex;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;

public class DBMigrationScriptsChecksumTest {
    private static final String SEPARATOR = ", ";

    @Test
    public void shouldVerifyThatAllMigrationScriptsHaveProperChecksum() throws IOException {
        final Map<String, String> existingChecksumsMap = newHashMap();

        final String[] checksums = readFileToString(new File(SCRIPT_DIRECTORY_PATH + "/checksums.txt"), "UTF8").split("\n");
        for (String checksum : checksums) {
            existingChecksumsMap.put(
                    substringBefore(checksum, SEPARATOR), substringAfter(checksum, SEPARATOR)
            );
        }

        final Map<String, String> newChecksums = newHashMap();
        final Collection<File> files = listFiles(new File(SCRIPT_DIRECTORY_PATH), new String[]{"sql"}, true);
        for (File file : files) {
            newChecksums.put(file.getName(), md5DigestAsHex(new FileInputStream(file)));
        }

        for (Map.Entry<String, String> existingChecksum : existingChecksumsMap.entrySet()) {
            final String existingChecksumKey = existingChecksum.getKey();
            if (!newChecksums.containsKey(existingChecksumKey)) {
                fail(
                        "Looks like there's more checksums in checksums file. Have you deleted the following files by any chance?\n" + existingChecksumKey
                );
            }

            if (!newChecksums.get(existingChecksumKey).equals(existingChecksum.getValue())) {
                fail(
                        "You SHOULD NOT change DB Migrations that have already been executed! (verify checksums)\n" + existingChecksumKey
                );
            }

            newChecksums.remove(existingChecksumKey);
        }

        if (newChecksums.size() > 0) {
            String missedChecksums = EMPTY;
            for (Map.Entry<String, String> newChecksum : newChecksums.entrySet()) {
                missedChecksums += newChecksum.getKey() + SEPARATOR + newChecksum.getValue() + "\n";
            }
            fail(
                    "\n*****************************************************************************************\n"
                            + "New DB migration/s has/ve been added. The following line/s MUST be added to checksums.txt\n" + missedChecksums
                            + "*****************************************************************************************\n"
                            + "You MUST add the new checksum/s value/s to the checksums.txt file"
                            + "*****************************************************************************************\n"
            );
        }
    }
}