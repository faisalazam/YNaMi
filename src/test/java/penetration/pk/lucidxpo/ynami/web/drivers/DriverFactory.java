package penetration.pk.lucidxpo.ynami.web.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import penetration.pk.lucidxpo.ynami.config.Config;

import java.io.File;

import static io.fluentlenium.configuration.PredefinedDesiredCapabilities.chrome;
import static io.fluentlenium.configuration.PredefinedDesiredCapabilities.firefox;
import static io.fluentlenium.configuration.PredefinedDesiredCapabilities.htmlUnit;
import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static java.io.File.separator;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.lang.System.setProperty;
import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.chrome.ChromeOptions.CAPABILITY;
//import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;
//import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
//import static org.openqa.selenium.remote.DesiredCapabilities.chrome;
//import static org.openqa.selenium.remote.DesiredCapabilities.firefox;
//import static org.openqa.selenium.remote.DesiredCapabilities.htmlUnit;

@Slf4j
public class DriverFactory {
    private final static int TIMEOUT = 10;
    private static final String PROXY = "proxy";
    private static final String CHROME = "chrome";
    private static final String FIREFOX = "firefox";
    private static final String HTMLUNIT = "htmlunit";
    private static final String HEADLESS = "--headless";
    private static final String TEST_TYPE = "--test-type";
    private static final String NO_SANDBOX = "--no-sandbox";
    private static final String HEADLESS_MODE = "headless_mode";
    private static final String START_MAXIMIZED = "--start-maximized";
    private static final String DISABLE_DEV_SHM_USAGE = "--disable-dev-shm-usage";

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
        if (CHROME.equalsIgnoreCase(type)) {
            return createChromeDriver(new DesiredCapabilities());
        } else if (FIREFOX.equalsIgnoreCase(type)) {
            return createFirefoxDriver(null);
        } else if (HTMLUNIT.equalsIgnoreCase(type)) {
            return createHtmlUnitDriver(null);
        }
        throw new RuntimeException("Unsupported WebDriver browser: " + type);
    }

    private WebDriver createProxyDriver(final String type) {
        if (CHROME.equalsIgnoreCase(type)) {
            return createChromeDriver(createProxyCapabilities(CHROME));
        } else if (FIREFOX.equalsIgnoreCase(type)) {
            return createFirefoxDriver(createProxyCapabilities(FIREFOX));
        } else if (HTMLUNIT.equalsIgnoreCase(type)) {
            return createHtmlUnitDriver(createProxyCapabilities(HTMLUNIT));
        }
        throw new RuntimeException("Unsupported WebDriver browser: " + type);
    }

    private WebDriver createChromeDriver(final DesiredCapabilities capabilities) {
//        setProperty("webdriver.chrome.driver", Config.getInstance().getDefaultDriverPath());
        final WebDriverManager webDriverManager = chromedriver();
        if (capabilities != null) {
            final ChromeOptions options = new ChromeOptions();
            options.setAcceptInsecureCerts(true);
            options.addArguments(
                    TEST_TYPE,
                    NO_SANDBOX,
                    START_MAXIMIZED,
                    DISABLE_DEV_SHM_USAGE
            );

            // default setting will be headless mode
            final String headlessMode = getenv(HEADLESS_MODE);
            if (headlessMode == null || TRUE.equals(valueOf(headlessMode))) {
                options.addArguments(HEADLESS);
            }

            capabilities.setCapability(CAPABILITY, options);
            options.merge(capabilities);
            webDriverManager.capabilities(options);
        }
        webDriverManager.setup();
        final WebDriver driver = webDriverManager.create();
        driver.manage().timeouts().implicitlyWait(ofSeconds(TIMEOUT));
        return driver;
    }

    private WebDriver createHtmlUnitDriver(DesiredCapabilities capabilities) {
        if (capabilities != null) {
            capabilities.setBrowserName(HTMLUNIT);
            return new HtmlUnitDriver(capabilities);
        }
        capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(HTMLUNIT);
        capabilities.setAcceptInsecureCerts(true);
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
        capabilities.setCapability("profile", myProfile);
//        capabilities.setCapability(PROFILE, myProfile);
        setProperty("webdriver.gecko.driver", Config.getInstance().getDefaultDriverPath());
        return new FirefoxDriver(new FirefoxOptions(capabilities));
    }

    private DesiredCapabilities createProxyCapabilities(final String type) {
        DesiredCapabilities capabilities = null;
        switch (type) {
            case CHROME -> capabilities = chrome();
            case FIREFOX -> capabilities = firefox();
            case HTMLUNIT -> capabilities = htmlUnit();
            default -> {
            }
        }
        final Proxy proxy = new Proxy();
        final Config instance = Config.getInstance();
        proxy.setHttpProxy(instance.getProxyHost() + ":" + instance.getProxyPort());
        proxy.setSslProxy(instance.getProxyHost() + ":" + instance.getProxyPort());
        assert capabilities != null;
        capabilities.setCapability(PROXY, proxy);
        return capabilities;
    }
}
