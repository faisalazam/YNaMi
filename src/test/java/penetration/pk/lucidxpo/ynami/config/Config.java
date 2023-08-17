package penetration.pk.lucidxpo.ynami.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.io.FileHandler;
import penetration.pk.lucidxpo.ynami.exceptions.ConfigurationException;
import penetration.pk.lucidxpo.ynami.model.Credentials;
import penetration.pk.lucidxpo.ynami.model.User;
import penetration.pk.lucidxpo.ynami.model.UserPassCredentials;
import penetration.pk.lucidxpo.ynami.web.Application;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.io.File.separator;
import static java.lang.Class.forName;
import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static java.lang.System.getenv;
import static java.lang.System.out;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static penetration.pk.lucidxpo.ynami.zaputils.ZapInfo.builder;
import static penetration.pk.lucidxpo.ynami.zaputils.boot.AbstractZapBoot.API_KEY;
import static penetration.pk.lucidxpo.ynami.zaputils.boot.Zap.startZap;

@Slf4j
public class Config {
    private String proxyApi;
    private String proxyHost;
    private int proxyPort = 0;
    private XMLConfiguration xml;
    private static Config config;

    private static final int DEFAULT_MAX_DEPTH = 10;
    private static final int DEFAULT_SECURE_PORT = 443;
    private static final int DEFAULT_INSECURE_PORT = 80;
    private static final String PEN_TEST_CONFIG_XML_PATH = "src/test/java/penetration/pk/lucidxpo/ynami/config/config.xml";

    public Application createApp() {
        final Object app;
        try {
            final Class<?> appClass = forName(getInstance().getClassName());
            app = appClass.getDeclaredConstructor().newInstance();
            return (Application) app;
        } catch (final Exception e) {
            log.warn("Error instantiating the class defined in config.xml");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public synchronized static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public User getDefaultUser() {
        return new User(getDefaultCredentials());
    }

    public Credentials getDefaultCredentials() {
        return new UserPassCredentials(
                validateAndGetString("defaultUsername"),
                validateAndGetString("defaultPassword"));
    }

    public List<String> getIgnoreUrls() {
        final List<String> ignoreUrls = newArrayList();
        getXml().configurationsAt("scanner.ignoreUrl").forEach(ignoreUrl -> {
            // TODO Upgrade: fix and uncomment me
//            ignoreUrls.add(ignoreUrl.getRoot().getValue().toString());
            out.println(ignoreUrl);
        });
        return ignoreUrls;
    }

    public List<String> getSpiderUrls() {
        // TODO Upgrade: fix and uncomment me instead of returning emptyList()
        return emptyList();
//        getXml().configurationsAt("scanner.spiderUrl").stream()
//                .map(ignoreUrl -> ignoreUrl.getRoot().getValue().toString())
//                .collect(toList());
    }

    public int getMaxDepth() {
        final String portAsString = validateAndGetString("scanner.maxDepth");
        if (isNotBlank(portAsString)) {
            return parseInt(portAsString);
        }
        return DEFAULT_MAX_DEPTH;
    }

    public String getBaseUrl() {
        return validateAndGetString("baseUrl");
    }

    public String getSSLyzePath() {
        return validateAndGetString("sslyze.path");
    }

    public String getSSLyzeOption() {
        return validateAndGetString("sslyze.option");
    }

    public String getDefaultDriver() {
        String driver = "htmlunit";
        try {
            driver = validateAndGetString("defaultDriver");
        } catch (final RuntimeException ignored) {
        }
        return driver;
    }

    public String getDefaultDriverPath() {
        String path;
        try {
            path = validateAndGetString("defaultDriver[@path]");
            return path;
        } catch (final RuntimeException e) {
            log.info("No path to the defaultDriver specified in config.xml, using auto-detection.");
            //Option path not specified
            if (IS_OS_MAC_OSX) {
                path = "drivers" + separator + "chromedriver-mac";
            } else if (IS_OS_WINDOWS) {
                path = "drivers" + separator + "chromedriver.exe";
            } else if (IS_OS_LINUX) {
                throw new RuntimeException("Linux detected, please specify the correct chrome driver to use (32 or 64 bit) in the config.xml file");
            } else {
                throw new RuntimeException("Could not determine host OS. Specify the correct chrome driver to use for this OS in the config.xml file");
            }
            log.info("Using driver at: " + path);
            return path;
        }
    }

    public String getProxyHost() {
        if (proxyHost != null) {
            return proxyHost;
        }

        try {
            proxyHost = validateAndGetString("proxy.host");
            proxyPort = parseInt(validateAndGetString("proxy.port"));
            proxyApi = validateAndGetString("proxy.api");
        } catch (final RuntimeException e) {
            try {
                final ZapInfo zapInfo = builder().buildToRunZap(getInstance().getZapPath());
                startZap(zapInfo);
                proxyPort = zapInfo.getPort();
                proxyHost = "127.0.0.1";
                proxyApi = API_KEY;
            } catch (final Exception re) {
                log.warn("Error starting embedded ZAP");
                re.printStackTrace();
            }
        }
        return getInstance().proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyApi() {
        return proxyApi;
    }

    public void setProxyApi(final String key) {
        proxyApi = key;
    }

    public String getNoProxyHosts() {
        return join(",", validateAndGetStringArray("upstreamProxy.noProxyHosts"));
    }

    public String getUpstreamProxyHost() {
        return validateAndGetString("upstreamProxy.host");
    }

    public int getUpstreamProxyPort() {
        final String portAsString = validateAndGetString("upstreamProxy.port");
        if (portAsString.length() > 0) {
            return parseInt(portAsString);
        }
        return DEFAULT_INSECURE_PORT;
    }

    public String getSslHost() {
        return validateAndGetString("sslyze.targetHost");
    }

    public int getSslPort() {
        final String portAsString = validateAndGetString("sslyze.targetPort");
        if (portAsString.length() > 0) {
            return parseInt(portAsString);
        }
        return DEFAULT_SECURE_PORT;
    }

    public Set<String> getSessionIDs() {
        return getXml().getList("sessionIds.name").stream().map(o -> (String) o).collect(toSet());
    }

    public String getIncorrectUsername() {
        return validateAndGetString("incorrectUsername");
    }

    public String getIncorrectPassword() {
        return validateAndGetString("incorrectPassword");
    }

    public String getNessusUsername() {
        return validateAndGetString("nessus.username");
    }

    public String getNessusPassword() {
        return validateAndGetString("nessus.password");
    }

    public boolean displayStackTrace() {
        return getXml().getBoolean("displayStackTrace");
    }

    public String getLatestReportsDir() {
        return validateAndGetString("latestReportsDir");
    }

    public String getReportsDir() {
        return validateAndGetString("reportsDir");
    }

    private Config() {
//        configure("log4j.properties");
        loadConfig(PEN_TEST_CONFIG_XML_PATH);
    }

    private String getClassName() {
        return validateAndGetString("class");
    }

    private String getZapPath() {
        return validateAndGetString("zapPath");
    }

    private static XMLConfiguration getXml() {
        return getInstance().xml;
    }

    @SuppressWarnings("SameParameterValue")
    private void loadConfig(final String file) {
        try {
            // TODO Upgrade: see which of the following solutions work?
            // This issue is coming due to the upgrade/replacement of commons-configuration » commons-configuration
            // with org.apache.commons » commons-configuration2
            // solution 1
//            final URL url = this.getClass().getResource(file);
//            xml = new XMLConfiguration();
//            assert url != null;
//            xml.read(url.openStream());

            // solution 2
            xml = new BasicConfigurationBuilder<>(XMLConfiguration.class)
                    .configure(new Parameters().xml())
//            xml = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
//                    .configure(new Parameters().fileBased().setFileName(file))
                    .getConfiguration();
            final FileHandler fileHandler = new FileHandler(xml);
            fileHandler.load(file);


            // solution 3
//            Parameters params = new Parameters();
//            FileBasedConfigurationBuilder<XMLConfiguration> fileBuilder =
//                    new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
//                            .configure(params.fileBased().setFileName("/tmp/dummy.xml"));`
//
//            XMLConfiguration xmlConfiguration = fileBuilder.getConfiguration();
//            xmlConfiguration.read(inputStream);
        } catch (final ConfigurationException | org.apache.commons.configuration2.ex.ConfigurationException cex) {
            cex.printStackTrace();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private String[] validateAndGetStringArray(final String value) {
        final String[] ret = getXml().getStringArray(value);
        if (ret == null) {
            throw new RuntimeException(value + " not defined in config.xml");
        }
        return ret;
    }

    private String validateAndGetString(final String value) {
        String ret = getenv(value);
        if (ret != null) {
            return ret;
        }
        ret = getXml().getString(value);
        if (ret == null) {
            throw new RuntimeException(value + " not defined in config.xml");
        }
        return ret;
    }
}