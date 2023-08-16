package migration.pk.lucidxpo.ynami.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

class DBMigrationScriptsChecksumTest {
    private static final String SEPARATOR = ",";

    @Test
    void shouldVerifyThatAllMigrationScriptsHaveProperChecksum() throws IOException {
        final Map<String, String> existingChecksumsMap = newHashMap();

        final String[] checksums = readFileToString(
                new File(SCRIPT_DIRECTORY_PATH + "/checksums.txt"),
                "UTF8"
        ).split("\n");
        for (String checksum : checksums) {
            if (isNotBlank(checksum)) {
                existingChecksumsMap.put(
                        substringBefore(checksum, SEPARATOR).trim(), substringAfter(checksum, SEPARATOR).trim()
                );
            }
        }

        final Map<String, String> newChecksums = newHashMap();
        final Collection<File> files = listFiles(new File(SCRIPT_DIRECTORY_PATH), new String[]{"sql"}, true);
        for (File file : files) {
            // 'md5DigestAsHex' will generate the new checksum for the sql file
            newChecksums.put(file.getName(), md5DigestAsHex(new FileInputStream(file)));
        }

        for (Map.Entry<String, String> existingChecksum : existingChecksumsMap.entrySet()) {
            final String existingChecksumKey = existingChecksum.getKey();
            if (!newChecksums.containsKey(existingChecksumKey)) {
                fail(
                        "Looks like there's more checksums in checksums file. " +
                                "Have you deleted the following files by any chance?\n" +
                                existingChecksumKey
                );
            }

            if (!newChecksums.get(existingChecksumKey).equals(existingChecksum.getValue())) {
                fail(
                        "You SHOULD NOT change DB Migrations that have already been executed! (verify checksums)\n" +
                                existingChecksumKey
                );
            }

            newChecksums.remove(existingChecksumKey);
        }

        if (newChecksums.size() > 0) {
            StringBuilder missedChecksums = new StringBuilder(EMPTY);
            for (Map.Entry<String, String> newChecksum : newChecksums.entrySet()) {
                missedChecksums.append(newChecksum.getKey()).append(SEPARATOR).append(newChecksum.getValue()).append("\n");
            }
            fail(
                    "\n*****************************************************************************************\n"
                            + "New DB migration/s has/ve been added. The following line/s MUST be added to checksums.txt\n"
                            + missedChecksums
                            + "*****************************************************************************************\n"
                            + "You MUST add the new checksum/s value/s to the checksums.txt file"
                            + "*****************************************************************************************\n"
            );
        }
    }
}
