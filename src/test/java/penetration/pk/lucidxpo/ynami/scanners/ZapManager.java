package penetration.pk.lucidxpo.ynami.scanners;

import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.config.Config;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.apache.commons.io.FileUtils.deleteDirectory;

@Slf4j
public class ZapManager {
    private int port;
    private Process process;
    private final String HOST = "127.0.0.1";
    private static ZapManager instance = null;
    public static final String API_KEY = "zapapisecret";

    private ZapManager() {
    }

    public static synchronized ZapManager getInstance() {
        if (instance == null) {
            instance = new ZapManager();
        }
        return instance;
    }

    public int startZAP(final String zapPath) throws Exception {
        if (process == null) {
            final File zapProgramFile = new File(zapPath);
            port = findOpenPortOnAllLocalInterfaces();
            final List<String> params = newArrayList(
                    zapProgramFile.getAbsolutePath(),
                    "-daemon",
                    "-host",
                    HOST,
                    "-port",
                    valueOf(port),
                    "-dir",
                    "tmp",
                    "-config",
                    "scanner.threadPerHost=20",
                    "-config",
                    "spider.thread=10",
                    "-config",
                    "api.key=" + API_KEY
            );

            Config.getInstance().setProxyApi(API_KEY);
            final String upstreamProxyHost = Config.getInstance().getUpstreamProxyHost();
            if (!upstreamProxyHost.isEmpty()) {
                final int upstreamProxyPort = Config.getInstance().getUpstreamProxyPort();
                log.info("Setting upstream proxy for ZAP to: " + upstreamProxyHost + ":" + upstreamProxyPort);
                params.add("-config");
                params.add("connection.proxyChain.hostName=" + upstreamProxyHost);
                params.add("-config");
                params.add("connection.proxyChain.port=" + upstreamProxyPort);
                params.add("-config");
                params.add("connection.proxyChain.enabled=true");
            }
            log.info("Start ZAProxy [" + zapProgramFile.getAbsolutePath() + "] on port: " + port);
            final ProcessBuilder processBuilder = new ProcessBuilder().inheritIO();
            processBuilder.directory(zapProgramFile.getParentFile());
            process = processBuilder.command(params.toArray(new String[0])).start();
            waitForSuccessfulConnectionToZap();
        } else {
            log.info("ZAP already started.");
        }
        return port;
    }

    public void stopZap() throws IOException {
        if (process == null) {
            return; //ZAP not running
        }
        try {
            log.info("Stopping ZAP");
//            final ClientApi client = new ClientApi(HOST, port, API_KEY);
//            client.core.shutdown();
//            sleep(2000);
            process.destroy();
        } catch (final Exception e) {
            log.warn("Error shutting down ZAP.");
            log.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            deleteDirectory(new File("src/test/java/penetration/pk/lucidxpo/ynami/zap/tmp"));
        }
    }

    private void waitForSuccessfulConnectionToZap() {
        //milliseconds
        int connectionTimeoutInMs = 15000;
        final int pollingIntervalInMs = 1000;
        boolean connectionSuccessful = false;
        final long startTime = currentTimeMillis();
        Socket socket = null;
        do {
            try {
                log.info("Attempting to connect to ZAP API on: " + HOST + " port: " + port);
                socket = new Socket();
                socket.connect(new InetSocketAddress(HOST, port), connectionTimeoutInMs);
                connectionSuccessful = true;
                log.info("Connected to ZAP");
            } catch (final SocketTimeoutException ignore) {
                throw new RuntimeException("Unable to connect to ZAP's proxy after " + 15000 + " milliseconds.");
            } catch (final IOException ignore) {
                // and keep trying but wait some time first...
                try {
                    sleep(pollingIntervalInMs);
                } catch (final InterruptedException e) {
                    throw new RuntimeException("The task was interrupted while sleeping between connection polling.", e);
                }

                final long ellapsedTime = currentTimeMillis() - startTime;
                if (ellapsedTime >= 15000) {
                    throw new RuntimeException("Unable to connect to ZAP's proxy after " + 15000 + " milliseconds.");
                }
                connectionTimeoutInMs = (int) (15000 - ellapsedTime);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while (!connectionSuccessful);
    }

    private Integer findOpenPortOnAllLocalInterfaces() throws IOException {
        try (
                final ServerSocket socket = new ServerSocket(0)
        ) {
            port = socket.getLocalPort();
            socket.close();
            return port;
        }
    }
}