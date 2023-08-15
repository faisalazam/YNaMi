package penetration.pk.lucidxpo.ynami.web;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import penetration.pk.lucidxpo.ynami.clients.AuthTokenManager;
import penetration.pk.lucidxpo.ynami.clients.AuthTokenManagerImpl;
import penetration.pk.lucidxpo.ynami.config.Config;
import penetration.pk.lucidxpo.ynami.exceptions.UnexpectedContentException;

import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static penetration.pk.lucidxpo.ynami.web.drivers.DriverFactory.getDriver;
import static penetration.pk.lucidxpo.ynami.web.drivers.DriverFactory.getProxyDriver;

@Slf4j
public class WebApplication extends Application {
    private AuthTokenManagerImpl authTokenManagerImpl;

    protected WebDriver driver;

    protected WebApplication() {
        setImplicitWait(3, SECONDS);
    }

    private void setAuthTokenManagerImpl(final AuthTokenManagerImpl authTokenManagerImpl) {
        this.authTokenManagerImpl = authTokenManagerImpl;
        this.driver = authTokenManagerImpl.getDriver();
    }

    public AuthTokenManagerImpl getAuthTokenManagerImpl() {
        return authTokenManagerImpl;
    }

    public void verifyTextPresent(final String text) {
        if (!authTokenManagerImpl.getDriver().getPageSource().contains(text)) {
            throw new UnexpectedContentException("Expected text: [" + text + "] was not found.");
        }
    }

    protected WebElement findAndWaitForElement(final By by) {
        try {
            final WebDriverWait wait = new WebDriverWait(authTokenManagerImpl.getDriver(), ofSeconds(10));
            wait.until(visibilityOfElementLocated(by));
        } catch (final TimeoutException e) {
            throw new NoSuchElementException(e.getMessage());
        }
        return authTokenManagerImpl.getDriver().findElement(by);
    }

    public void navigate() {
        authTokenManagerImpl.getDriver().get(Config.getInstance().getBaseUrl());
    }

    @Override
    public void enableHttpLoggingClient() {
        setAuthTokenManagerImpl(new AuthTokenManagerImpl(getProxyDriver(Config.getInstance().getDefaultDriver())));
    }

    @Override
    public void enableDefaultClient() {
        setAuthTokenManagerImpl(new AuthTokenManagerImpl(getDriver(Config.getInstance().getDefaultDriver())));
    }

    @Override
    public AuthTokenManager getAuthTokenManager() {
        return authTokenManagerImpl;
    }

    @SuppressWarnings("SameParameterValue")
    private void setImplicitWait(final long time, final TimeUnit unit) {
        getDriver(Config.getInstance().getDefaultDriver()).manage().timeouts().implicitlyWait(time, unit);
        getProxyDriver(Config.getInstance().getDefaultDriver()).manage().timeouts().implicitlyWait(time, unit);
    }
}
