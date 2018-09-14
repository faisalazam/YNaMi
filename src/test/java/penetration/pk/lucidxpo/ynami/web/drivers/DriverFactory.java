package penetration.pk.lucidxpo.ynami.web.drivers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import penetration.pk.lucidxpo.ynami.config.Config;

import java.io.File;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.openqa.selenium.chrome.ChromeOptions.CAPABILITY;
import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
import static org.openqa.selenium.remote.DesiredCapabilities.chrome;
import static org.openqa.selenium.remote.DesiredCapabilities.firefox;
import static org.openqa.selenium.remote.DesiredCapabilities.htmlUnit;

@Slf4j
public class DriverFactory {
    private final static String CHROME = "chrome";
    private final static String FIREFOX = "firefox";
    private final static String HTMLUNIT = "htmlunit";

    private static DriverFactory dm;
    private static WebDriver driver;
    private static WebDriver proxyDriver;


    private static DriverFactory getInstance() {
        if (dm == null) {
            dm = new DriverFactory();
        }
        return dm;
    }

    public static WebDriver getProxyDriver(final String name) {
        return getDriver(name, true);
    }

    public static WebDriver getDriver(final String name) {
        return getDriver(name, false);
    }


    // Return the desired driver and clear all its cookies
    private static WebDriver getDriver(final String type, final boolean isProxyDriver) {
        final WebDriver retVal = getInstance().findOrCreate(type, isProxyDriver);
        try {
            if (!retVal.getCurrentUrl().equals("about:blank")) {
                retVal.manage().deleteAllCookies();
            }
        } catch (final Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return retVal;
    }


    public static void quitAll() {
        log.debug("closing all webDrivers");
        try {
            if (driver != null) {
                driver.quit();
            }
            if (proxyDriver != null) {
                proxyDriver.quit();
            }
        } catch (final Exception e) {
            log.error("Error quitting webDriver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Re-use drivers to reduce startup times
     */
    private WebDriver findOrCreate(final String type, final boolean isProxyDriver) {
        if (isProxyDriver) {
            if (proxyDriver != null) {
                return proxyDriver;
            }
            proxyDriver = createProxyDriver(type);
            return proxyDriver;
        } else {
            if (driver != null) {
                return driver;
            }
            driver = createDriver(type);
            return driver;
        }
    }

    private WebDriver createDriver(final String type) {
        if (type.equalsIgnoreCase(CHROME)) {
            return createChromeDriver(new DesiredCapabilities());
        } else if (type.equalsIgnoreCase(FIREFOX)) {
            return createFirefoxDriver(null);
        } else if (type.equalsIgnoreCase(HTMLUNIT)) {
            return createHtmlUnitDriver(null);
        }
        throw new RuntimeException("Unsupported WebDriver browser: " + type);
    }

    private WebDriver createProxyDriver(final String type) {
        if (type.equalsIgnoreCase(CHROME)) {
            return createChromeDriver(createProxyCapabilities(CHROME));
        } else if (type.equalsIgnoreCase(FIREFOX)) {
            return createFirefoxDriver(createProxyCapabilities(FIREFOX));
        } else if (type.equalsIgnoreCase(HTMLUNIT)) {
            return createHtmlUnitDriver(createProxyCapabilities(HTMLUNIT));
        }
        throw new RuntimeException("Unsupported WebDriver browser: " + type);
    }

    private WebDriver createChromeDriver(final DesiredCapabilities capabilities) {
        setProperty("webdriver.chrome.driver", Config.getInstance().getDefaultDriverPath());
        if (capabilities != null) {
            capabilities.setCapability(ACCEPT_SSL_CERTS, true);
            final ChromeOptions options = new ChromeOptions();
            options.addArguments("--test-type");
            capabilities.setCapability(CAPABILITY, options);
            return new ChromeDriver(capabilities);
        } else {
            return new ChromeDriver();
        }
    }

    private WebDriver createHtmlUnitDriver(DesiredCapabilities capabilities) {
        if (capabilities != null) {
            capabilities.setBrowserName("htmlunit");
            return new HtmlUnitDriver(capabilities);
        }
        capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("htmlunit");
        capabilities.setCapability(ACCEPT_SSL_CERTS, true);
        return new HtmlUnitDriver(capabilities);
    }

    private WebDriver createFirefoxDriver(DesiredCapabilities capabilities) {

        final ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile myProfile = allProfiles.getProfile("WebDriver");
        if (myProfile == null) {
            final File ffDir = new File(getProperty("user.dir") + separator + "ffProfile");
            if (!ffDir.exists()) {
                ffDir.mkdir();
            }
            myProfile = new FirefoxProfile(ffDir);
        }
        myProfile.setAcceptUntrustedCertificates(true);
        myProfile.setAssumeUntrustedCertificateIssuer(true);
        myProfile.setPreference("webdriver.load.strategy", "unstable");
        final String noProxyHosts = Config.getInstance().getNoProxyHosts();
        if (!noProxyHosts.isEmpty()) {
            myProfile.setPreference("network.proxy.no_proxies_on", noProxyHosts);
        }
        if (capabilities == null) {
            capabilities = new DesiredCapabilities();
        }
        capabilities.setCapability(PROFILE, myProfile);
        setProperty("webdriver.gecko.driver", Config.getInstance().getDefaultDriverPath());
        return new FirefoxDriver(new FirefoxOptions(capabilities));
    }

    private DesiredCapabilities createProxyCapabilities(final String type) {
        DesiredCapabilities capabilities = null;
        switch (type) {
            case CHROME:
                capabilities = chrome();
                break;
            case FIREFOX:
                capabilities = firefox();
                break;
            case HTMLUNIT:
                capabilities = htmlUnit();
                break;
            default:
                break;
        }
        final Proxy proxy = new Proxy();
        final Config instance = Config.getInstance();
        proxy.setHttpProxy(instance.getProxyHost() + ":" + instance.getProxyPort());
        proxy.setSslProxy(instance.getProxyHost() + ":" + instance.getProxyPort());
        capabilities.setCapability("proxy", proxy);
        return capabilities;
    }
}
