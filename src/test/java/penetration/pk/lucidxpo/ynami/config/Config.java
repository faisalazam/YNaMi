package penetration.pk.lucidxpo.ynami.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.XMLConfiguration;
import penetration.pk.lucidxpo.ynami.exceptions.ConfigurationException;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import static java.io.File.separator;
import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static java.lang.System.getenv;
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

    private static final String PEN_TEST_CONFIG_XML_PATH = "src/test/java/penetration/pk/lucidxpo/ynami/config/config.xml";

    public synchronized static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    private Config() {
//        configure("log4j.properties");
        loadConfig(PEN_TEST_CONFIG_XML_PATH);
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

    /*
        If value is defined as a system environment variable, then return its value.
        If not, then search for it in the config file
    */
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

    @SuppressWarnings("SameParameterValue")
    private String[] validateAndGetStringArray(final String value) {
        final String[] ret = getXml().getStringArray(value);
        if (ret == null) {
            throw new RuntimeException(value + " not defined in config.xml");
        }
        return ret;
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

    public String getZapPath() {
        return validateAndGetString("zapPath");
    }

    public String getNoProxyHosts() {
        return join(",", validateAndGetStringArray("upstreamProxy.noProxyHosts"));
    }

    public String getUpstreamProxyHost() {
        return validateAndGetString("upstreamProxy.host");
    }

    public int getUpstreamProxyPort() {
        final String portAsString = validateAndGetString("upstreamProxy.port");
        if (portAsString != null && portAsString.length() > 0) {
            return parseInt(portAsString);
        }
        return 80;
    }

    private static XMLConfiguration getXml() {
        return getInstance().xml;
    }

    @SuppressWarnings("SameParameterValue")
    private void loadConfig(final String file) {
        try {
            xml = new XMLConfiguration();
            xml.load(file);
        } catch (final ConfigurationException | org.apache.commons.configuration.ConfigurationException cex) {
            cex.printStackTrace();
        }
    }
}