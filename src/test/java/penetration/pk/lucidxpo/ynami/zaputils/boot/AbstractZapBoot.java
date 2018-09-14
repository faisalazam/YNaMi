package penetration.pk.lucidxpo.ynami.zaputils.boot;

import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;
import penetration.pk.lucidxpo.ynami.zaputils.exception.ZapInitializationTimeoutException;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static java.lang.Thread.currentThread;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Paths.get;
import static penetration.pk.lucidxpo.ynami.config.Config.getInstance;

/**
 * Base implementation for {@link ZapBoot}, responsible for the timeout logic.
 */
@Slf4j
public abstract class AbstractZapBoot implements ZapBoot {
    private static final String HEAD = "HEAD";
    private static final String DEFAULT_ZAP_LOG_FILE_NAME = "zap.log";
    private static final long ZAP_INITIALIZATION_POLLING_INTERVAL_IN_MILLIS = 5 * 1000;
    // If ZAP is automatically started, its log will be stored in [current working directory]/target/zap-reports, along with the generated reports
    private static final String DEFAULT_ZAP_LOG_PATH = getProperty("user.dir") + separator + "target" + separator + "zap-reports";

    public static final String API_KEY = "zapapisecret";

    private static Process zap;

    abstract String buildStartCommand(final ZapInfo zapInfo);

    @Override
    public void startZap(final ZapInfo zapInfo) {
        final int port = zapInfo.getPort();
        if (zap != null || isZapRunning(port)) {
            log.info("ZAP is already up and running! No attempts will be made to start ZAP.");
            return;
        }

        try {
            preStart();
            start(zapInfo);
            waitForZapInitialization(port, zapInfo.getInitializationTimeoutInMillis());
        } catch (final IOException e) {
            log.error("Error starting ZAP.", e);
        }
    }

    @Override
    public void stopZap() {
        if (zap == null) {
            return; //ZAP not running
        }
        try {
            log.info("Stopping ZAP");
//            final ClientApi client = new ClientApi(HOST, port, API_KEY);
//            client.core.shutdown();
//            sleep(2000);
            zap.destroy();
        } catch (final Exception e) {
            log.warn("Error shutting down ZAP.");
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getDefaultZapOptions() {
        final StringBuilder defaultZapOptions = new StringBuilder();
        defaultZapOptions.append("-daemon").append(" ");
        defaultZapOptions.append("-config api.key=").append(API_KEY).append(" ");
        defaultZapOptions.append("-config api.incerrordetails=true").append(" ");
//        defaultZapOptions.append("-config proxy.ip=127.0.0.1").append(" ");
        defaultZapOptions.append("-config scanner.threadPerHost=20").append(" ");
        defaultZapOptions.append("-config spider.thread=10").append(" ");

        final String upstreamProxyHost = getInstance().getUpstreamProxyHost();
        if (!upstreamProxyHost.isEmpty()) {
            final int upstreamProxyPort = getInstance().getUpstreamProxyPort();
            log.info("Setting upstream proxy for ZAP to: " + upstreamProxyHost + ":" + upstreamProxyPort);
            defaultZapOptions.append("-config connection.proxyChain.enabled=true").append(" ");
            defaultZapOptions.append("-config connection.proxyChain.port=").append(upstreamProxyPort).append(" ");
            defaultZapOptions.append("-config connection.proxyChain.hostName=").append(upstreamProxyHost).append(" ");
        }
        return defaultZapOptions.toString();
    }

    void preStart() throws IOException {
    }

    void setupProcessBuilder(final ZapInfo zapInfo, final ProcessBuilder processBuilder) {
    }

    private void start(final ZapInfo zapInfo) throws IOException {
        final String startCommand = buildStartCommand(zapInfo);
        final ProcessBuilder processBuilder = new ProcessBuilder(startCommand.split(" +")).inheritIO();
        setupProcessBuilder(zapInfo, processBuilder);

        createDirectories(get(DEFAULT_ZAP_LOG_PATH));
        processBuilder.redirectOutput(new File(DEFAULT_ZAP_LOG_PATH, DEFAULT_ZAP_LOG_FILE_NAME));

        getInstance().setProxyApi(API_KEY);

        log.info("Starting ZAP with command: {}", startCommand);
        zap = processBuilder.start();
    }


    private static boolean isZapRunning(final int port) {
        return isZapRunning("localhost", port);
    }

    private static boolean isZapRunning(final String host, final int port) {
        return getResponseFromZap(host, port) == HTTP_OK;
    }

    private static int getResponseFromZap(final String host, final int port) {
        if (host == null) {
            return -1;
        }

        final String url = "http://" + host + ":" + port;

        int responseCode = -1;
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(HEAD);
            responseCode = conn.getResponseCode();
        } catch (final ConnectException e) {
            log.debug("ZAP could not be reached at {}:{}.", host, port);
        } catch (final IOException e) {
            log.error("Error trying to get a response from ZAP.", e);
        }
        return responseCode;
    }

    private static void waitForZapInitialization(final int port, final long timeoutInMillis) {
        waitForZapInitialization("localhost", port, timeoutInMillis);
    }

    @SuppressWarnings("SameParameterValue")
    private static void waitForZapInitialization(final String host, final int port, final long timeoutInMillis) {
        final long startUpTime = currentTimeMillis();
        do {
            if (currentTimeMillis() - startUpTime > timeoutInMillis) {
                final String message = "ZAP did not start before the timeout (" + timeoutInMillis + " ms).";
                log.error(message);
                throw new ZapInitializationTimeoutException(message);
            }

            sleep(ZAP_INITIALIZATION_POLLING_INTERVAL_IN_MILLIS);
            log.info("Checking if ZAP has started at {}:{}...", host, port);
        } while (!isZapRunning(host, port));

        log.info("ZAP has started!");
    }

    @SuppressWarnings("SameParameterValue")
    private static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }
}