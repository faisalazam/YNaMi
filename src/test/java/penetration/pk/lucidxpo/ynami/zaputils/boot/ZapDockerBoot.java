package penetration.pk.lucidxpo.ynami.zaputils.boot;

import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Paths.get;
import static org.apache.commons.io.IOUtils.copy;
import static penetration.pk.lucidxpo.ynami.zaputils.authentication.AuthenticationScripts.RELATIVE_PATH;

/**
 * Class responsible to start and stop ZAP by running ZAP's Docker image.
 * <p>
 * <b>Docker must be installed locally for this to work.</b>
 * <p>
 * This will be used when {@code zapInfo.shouldRunWithDocker} is {@code true}.
 */
@Slf4j
class ZapDockerBoot extends AbstractZapBoot {
    private static final String DEFAULT_DOCKER_COMMAND = "docker run --rm";
    private static final String ZAP_IMAGE_OPTION = " -i owasp/zap2docker-stable zap.sh ";

    private static final String CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH = "/zap/scripts/";
    private static final String CAS_AUTH_SCRIPT_FILE_NAME = "cas-auth.js";

    ZapDockerBoot(final int port, final String host) {
        super(port, host);
    }

    @Override
    void preStart() throws IOException {
        copyCasAuthScriptFileToMappedFolder();
    }

    @Override
    String buildStartCommand(final ZapInfo zapInfo) {
        final StringBuilder startCommand = new StringBuilder(DEFAULT_DOCKER_COMMAND);
        appendVolumeOption(startCommand);
        appendPortOption(zapInfo, startCommand);
        startCommand.append(ZAP_IMAGE_OPTION);

        final String options = zapInfo.getOptions();
        startCommand.append(options != null ? options : getDefaultZapOptions());
        startCommand.append(" -host ").append(host);
        startCommand.append(" -port ").append(port);

        return startCommand.toString();
    }

    private static void appendVolumeOption(final StringBuilder startCommand) {
        startCommand.append(" -v ");
        startCommand.append(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH);
        startCommand.append(":");
        startCommand.append(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH);
        startCommand.append(":ro");
    }

    private static void appendPortOption(final ZapInfo zapInfo, final StringBuilder startCommand) {
        startCommand.append(" -p ");
        startCommand.append(zapInfo.getPort());
        startCommand.append(":");
        startCommand.append(zapInfo.getPort());
    }

    private static void copyCasAuthScriptFileToMappedFolder() throws IOException {
        createDirectories(get(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH));

        final File scriptFile = new File(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH, CAS_AUTH_SCRIPT_FILE_NAME);

        final InputStream casAuthScriptInputStream = ZapDockerBoot.class.getResourceAsStream(RELATIVE_PATH + CAS_AUTH_SCRIPT_FILE_NAME);
        try (final FileOutputStream fileOutputStream = new FileOutputStream(scriptFile)) {
            copy(casAuthScriptInputStream, fileOutputStream);
        } catch (final IOException e) {
            log.error("Error while trying to create the script file for CAS authentication in " + CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH + ". "
                    + "The analysis will continue but CAS authentication will work only if the script file can be accessed by ZAP's Docker image "
                    + "(a default volume is created in " + CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH + ").", e);
        }
    }
}
