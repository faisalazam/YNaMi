package penetration.pk.lucidxpo.ynami.zaputils;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.net.ServerSocket;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static penetration.pk.lucidxpo.ynami.zaputils.boot.AbstractZapBoot.getDefaultZapOptions;

/**
 * Class that represents the information about the ZAP instance that will be used.
 * <p>
 * Depending on how the instance of this class is built, you end up with one of these situations:
 * <ul>
 * <li>ZAP is up and running in a given {@code host} and {@code port};</li>
 * <li>ZAP is locally installed and will be automatically started (and stopped afterwards).</li>
 * <li>Docker is locally installed and ZAP's image will be automatically started (and stopped afterwards).</li>
 * </ul>
 *
 * @see ZapInfoBuilder#buildToUseRunningZap(String, int) Builder().buildToUseRunningZap()
 * @see ZapInfoBuilder#buildToRunZap(int, String, String) Builder().buildToRunZap()
 * @see ZapInfoBuilder#buildToRunZapWithDocker(int, String) Builder().buildToRunZapWithDocker()
 */
public final class ZapInfo {
    private static final String DEFAULT_KEY = "";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_JVM_OPTIONS = "-Xmx1024m";
    private static final Long DEFAULT_INITIALIZATION_TIMEOUT_IN_MILLIS = 120000L;

    private final String host;
    private final Integer port;
    private final String path;
    private final String apiKey;
    private final String options;
    private final String jmvOptions;
    private final boolean shouldRunWithDocker;
    private final Long initializationTimeoutInMillis;

    public static ZapInfoBuilder builder() {
        return new ZapInfoBuilder();
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getJmvOptions() {
        return jmvOptions;
    }

    public String getOptions() {
        return options;
    }

    public long getInitializationTimeoutInMillis() {
        return initializationTimeoutInMillis;
    }

    public boolean shouldRunWithDocker() {
        return shouldRunWithDocker;
    }

    @SuppressWarnings("JavadocReference")
    public static class ZapInfoBuilder {
        private String path;
        private Integer port;
        private boolean shouldRunWithDocker;
        private String host = DEFAULT_HOST;
        private String apiKey = DEFAULT_KEY;
        private String options = getDefaultZapOptions();
        private String jmvOptions = DEFAULT_JVM_OPTIONS;
        private Long initializationTimeoutInMillis = DEFAULT_INITIALIZATION_TIMEOUT_IN_MILLIS;

        /**
         * Use this if ZAP is up and running (locally or in a remote machine).
         *
         * @param host the host where ZAP is running (e.g. {@code localhost}, {@code 172.23.45.13}).
         * @param port the port where ZAP is running (e.g. {@code 8080}).
         * @return the built {@link ZapInfo} instance.
         */
        ZapInfo buildToUseRunningZap(final String host, final int port) {
            return host(host).port(port).build();
        }

        /**
         * Use this if ZAP is up and running (locally or in a remote machine).
         *
         * @param host   the host where ZAP is running (e.g. {@code localhost}, {@code 172.23.45.13}).
         * @param port   the port where ZAP is running (e.g. {@code 8080}).
         * @param apiKey the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToUseRunningZap(final String host, final int port, final String apiKey) {
            return host(host).port(port).apiKey(apiKey).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param path the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(final String path) throws IOException {
            return port(findOpenPortOnAllLocalInterfaces()).path(path).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param port the port where ZAP will run (e.g. {@code 8080}).
         * @param path the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(final int port, final String path) {
            return port(port).path(path).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param path    the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@link getDefaultZapOptions()}
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(final int port, final String path, final String options) {
            return port(port).path(path).options(options).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param path    the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@link getDefaultZapOptions()}
         * @param apiKey  the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(final int port, final String path, final String options, final String apiKey) {
            return port(port).path(path).options(options).apiKey(apiKey).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param port the port where ZAP will run (e.g. {@code 8080}).
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(final int port) {
            return shouldRunWithDocker(true).port(port).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@link getDefaultZapOptions()}
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(final String options) throws IOException {
            return shouldRunWithDocker(true).port(findOpenPortOnAllLocalInterfaces()).options(options).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@link getDefaultZapOptions()}
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(final int port, final String options) {
            return shouldRunWithDocker(true).port(port).options(options).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@link getDefaultZapOptions()}
         * @param apiKey  the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(final int port, final String options, final String apiKey) {
            return shouldRunWithDocker(true).port(port).options(options).apiKey(apiKey).build();
        }

        static Integer findOpenPortOnAllLocalInterfaces() throws IOException {
            try (
                    final ServerSocket socket = new ServerSocket(0)
            ) {
                final Integer port = socket.getLocalPort();
                socket.close();
                return port;
            }
        }

        /**
         * Sets the host where ZAP is running. Don't call this if you want ZAP to be started automatically.
         *
         * @param host the host where ZAP is running (e.g. {@code localhost}, {@code 172.23.45.13}).
         * @return this {@code Builder} instance.
         */
        ZapInfoBuilder host(final String host) {
            if (host != null) {
                this.host = host;
            }
            return this;
        }

        /**
         * Either sets the port where ZAP is running or the port where ZAP will run, if ZAP is to be started automatically.
         * <p>
         * If the {@code host} was set, then the {@code port} represents where ZAP is currently running on the host.
         * Otherwise, it represents the {@code port} where ZAP will run (locally or in a Docker image).
         *
         * @param port the port where ZAP is running or where ZAP will run (e.g. {@code 8080}).
         * @return this {@code Builder} instance.
         */
        ZapInfoBuilder port(final int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets the API key needed to access ZAP's API, in case the key is enabled (it is by default).
         *
         * @param apiKey the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return this {@code Builder} instance.
         */
        ZapInfoBuilder apiKey(final String apiKey) {
            if (apiKey != null) {
                this.apiKey = apiKey;
            }
            return this;
        }

        /**
         * Sets the path where ZAP is installed.
         * <p>
         * This should be used when ZAP is installed locally, so the API is able to automatically start ZAP.
         *
         * @param path the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @return this {@code Builder} instance.
         */
        ZapInfoBuilder path(final String path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the JVM options used to run ZAP.
         * <p>
         * This should be used when ZAP is installed locally and it is automatically started and stopped.
         *
         * @param jmvOptions the JVM options used to start ZAP.
         * @return this {@code Builder} instance.
         * @see #path(String)
         */
        public ZapInfoBuilder jmvOptions(final String jmvOptions) {
            this.jmvOptions = jmvOptions;
            return this;
        }

        /**
         * Sets the options used to start ZAP.
         * <p>
         * This should be used to overwrite the default options used to start ZAP (locally or in a Docker image).
         *
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@link getDefaultZapOptions()}
         * @return this {@code Builder} instance.
         */
        ZapInfoBuilder options(final String options) {
            if (options != null) {
                this.options = options;
            }
            return this;
        }

        /**
         * Sets the timeout in milliseconds for ZAP's initialization, if ZAP is to be started automatically.
         * The default value is {@link DEFAULT_INITIALIZATION_TIMEOUT_IN_MILLIS}.
         *
         * @param initializationTimeoutInMillis the timeout in milliseconds for ZAP's initialization.
         * @return this {@code Builder} instance.
         */
        public ZapInfoBuilder initializationTimeoutInMillis(final Long initializationTimeoutInMillis) {
            if (initializationTimeoutInMillis != null) {
                this.initializationTimeoutInMillis = initializationTimeoutInMillis;
            }
            return this;
        }

        /**
         * Use this to indicate that ZAP should be started automatically with Docker. This is {@code false} by default.
         *
         * @param shouldRunWithDocker {@code true} if ZAP should be automatically started with Docker, {@code false} otherwise.
         * @return this {@code Builder} instance.
         */
        @SuppressWarnings("SameParameterValue")
        ZapInfoBuilder shouldRunWithDocker(final boolean shouldRunWithDocker) {
            this.shouldRunWithDocker = shouldRunWithDocker;
            return this;
        }

        /**
         * Builds a {@link ZapInfo} instance based on the builder parameters.
         * <p>
         * You should probably use the other build methods, choosing the one that suits your needs.
         *
         * @return a {@link ZapInfo} instance.
         * @see #buildToUseRunningZap(String, int) buildToUseRunningZap()
         * @see #buildToRunZap(int, String, String) buildToRunZap()
         * @see #buildToRunZapWithDocker(int, String) buildToRunZapWithDocker()
         */
        ZapInfo build() {
            return new ZapInfo(this);
        }

    }

    private ZapInfo(final ZapInfoBuilder zapInfoBuilder) {
        this.host = zapInfoBuilder.host;
        this.port = zapInfoBuilder.port;
        this.path = zapInfoBuilder.path;
        this.apiKey = zapInfoBuilder.apiKey;
        this.options = zapInfoBuilder.options;
        this.jmvOptions = zapInfoBuilder.jmvOptions;
        this.shouldRunWithDocker = zapInfoBuilder.shouldRunWithDocker;
        this.initializationTimeoutInMillis = zapInfoBuilder.initializationTimeoutInMillis;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("host", host)
                .append("port", port)
                .append("apiKey", apiKey)
                .append("path", path)
                .append("jvmOptions", jmvOptions)
                .append("options", options)
                .append("initializationTimeout", initializationTimeoutInMillis)
                .append("shouldRunWithDocker", shouldRunWithDocker)
                .toString();
    }
}