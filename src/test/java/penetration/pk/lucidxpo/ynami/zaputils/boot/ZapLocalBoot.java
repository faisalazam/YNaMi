package penetration.pk.lucidxpo.ynami.zaputils.boot;

import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;
import penetration.pk.lucidxpo.ynami.zaputils.exception.ZapInitializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.System.getProperty;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Paths.get;

/**
 * Class responsible to start and stop ZAP locally.
 * <p>
 * <b>ZAP must be installed locally for this to work.</b>
 * <p>
 * This will normally be used when ZAP's {@code zapInfo.path} was provided and {@code shouldRunWithDocker} is false.
 */
@Slf4j
class ZapLocalBoot extends AbstractZapBoot {

    @Override
    void setupProcessBuilder(final ZapInfo zapInfo, final ProcessBuilder processBuilder) {
        processBuilder.directory(getZapWorkingDirectory(zapInfo));
    }

    @Override
    String buildStartCommand(final ZapInfo zapInfo) {
        final StringBuilder startCommand = new StringBuilder();

        startCommand.append("java").append(" ");
        startCommand.append(zapInfo.getJmvOptions()).append(" ");
        startCommand.append("-jar").append(" ");

        try {
            final String zapJarName = retrieveZapJarName(zapInfo.getPath());
            startCommand.append(zapJarName).append(" ");
        } catch (final IOException e) {
            log.error("Error retrieving ZAP's JAR file.");
        }

        final String options = zapInfo.getOptions();
        startCommand.append(options != null ? options : getDefaultZapOptions());
        startCommand.append(" -host ").append(zapInfo.getHost());
        startCommand.append(" -port ").append(zapInfo.getPort());

        return startCommand.toString();
    }

    private static String retrieveZapJarName(final String path) throws IOException {
        final Path zapPath = get(path);
        if (isJarFile(zapPath)) {
            final String filename = zapPath.getFileName().toString();
            log.debug("ZapPath points to the Jar file {}", filename);
            return filename;
        }

        log.debug("ZapPath points to the folder {}", zapPath.getFileName().toString());

        for (final Path p : newDirectoryStream(zapPath)) {
            if (isJarFile(p)) {
                final String filename = p.getFileName().toString();
                log.debug("Chosen Zap Jar file {}", filename);
                return filename;
            }
        }

        throw new ZapInitializationException("ZAP's JAR file was not found.");
    }

    private static boolean isJarFile(final Path path) {
        if (path == null) {
            return false;
        }

        if (!isRegularFile(path)) {
            return false;
        }

        final Path fileName = path.getFileName();

        if (fileName == null) {
            return false;
        }

        return fileName.toString().endsWith(".jar");
    }

    private static File getZapWorkingDirectory(final ZapInfo zapInfo) {
        final String fullPath = zapInfo.getPath();
        final File dir = new File(fullPath);
        if (dir.isDirectory()) {
            return dir;
        }

        if (dir.isFile()) {
            return dir.getParentFile();
        }

        return new File(getProperty("user.dir"));
    }
}