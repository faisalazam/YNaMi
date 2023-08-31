package bdd.pk.lucidxpo.ynami.webdriver.scope;

import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.support.SimpleThreadScope;

import static java.util.Objects.isNull;

/**
 * This class is responsible for getting and returning any beans annotated with {@link WebdriverBeanScope}, which in
 * this case, will be {@link WebDriver}, from the {@link ObjectFactory}.
 */
@SuppressWarnings("NullableProblems")
public class WebdriverScope extends SimpleThreadScope {
    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        final Object object = super.get(name, objectFactory);
        if (object instanceof RemoteWebDriver) { // Checking instanceof because HtmlUnitDriver is not a RemoteWebDriver.
            final SessionId sessionId = ((RemoteWebDriver) object).getSessionId();
            if (isNull(sessionId)) {
                super.remove(name);
                return super.get(name, objectFactory);
            }
        }
        return object;
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
    }
}
