package penetration.pk.lucidxpo.ynami.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.XMLConfiguration;
import penetration.pk.lucidxpo.ynami.exceptions.ConfigurationException;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;

@Slf4j
public class Config {
    private String proxyApi;
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

    public void setProxyApi(final String key) {
        proxyApi = key;
    }

    public String getZapPath() {
        return validateAndGetString("zapPath");
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