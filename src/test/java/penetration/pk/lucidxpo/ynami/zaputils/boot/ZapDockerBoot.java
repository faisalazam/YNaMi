package penetration.pk.lucidxpo.ynami.zaputils.boot;

import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import static java.lang.System.getProperty;

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

    private static final String CAS_AUTH_SCRIPTS_DEFAULT_DOCKER_PATH = "/zap/scripts/";
    private static final String CAS_AUTH_SCRIPTS_SRC_PATH = getProperty("user.dir") + "/src/test/resources/zap/scripts/";

    ZapDockerBoot(final int port, final String host) {
        super(port, host);
    }

    @Override
    String buildStartCommand(final ZapInfo zapInfo) {
        final StringBuilder startCommand = new StringBuilder(DEFAULT_DOCKER_COMMAND);
        appendVolumeOption(startCommand);
        appendPortOption(startCommand);
        startCommand.append(ZAP_IMAGE_OPTION);

        final String options = zapInfo.getOptions();
        startCommand.append(options != null ? options : getDefaultZapOptions());
        startCommand.append(" -port ").append(port);
        startCommand.append(" -host ").append("0.0.0.0");
        startCommand.append(" -config ").append("api.addrs.addr.name=.*");
        startCommand.append(" -config ").append("api.addrs.addr.regex=true");

        return startCommand.toString();
    }

    private void appendVolumeOption(final StringBuilder startCommand) {
        startCommand.append(" -v ");
        startCommand.append(CAS_AUTH_SCRIPTS_SRC_PATH);
        startCommand.append(":");
        startCommand.append(CAS_AUTH_SCRIPTS_DEFAULT_DOCKER_PATH);
        startCommand.append(":ro");
    }

    private void appendPortOption(final StringBuilder startCommand) {
        startCommand.append(" -p ");
        startCommand.append(port);
        startCommand.append(":");
        startCommand.append(port);
    }
}
