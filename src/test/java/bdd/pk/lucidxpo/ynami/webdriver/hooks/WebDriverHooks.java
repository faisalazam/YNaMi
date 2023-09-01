package bdd.pk.lucidxpo.ynami.webdriver.hooks;

import bdd.pk.lucidxpo.ynami.annotations.LazyAutowired;
import bdd.pk.lucidxpo.ynami.config.AbstractSteps;
import io.cucumber.java.After;
import org.fluentlenium.adapter.IFluentAdapter;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Map;

/**
 * Hooks for managing the lifecycle of {@link WebDriver}.
 */
public class WebDriverHooks {
    @LazyAutowired
    private ApplicationContext applicationContext;

    @After
    public void afterScenario() {
        final WebDriver webDriver = this.applicationContext.getBean(WebDriver.class);
        getFluentAdapters().forEach(IFluentAdapter::releaseFluent);
        webDriver.quit();
    }

    /**
     * All of the *Steps classes (in {@link bdd.pk.lucidxpo.ynami.steps}) are extended from {@link AbstractSteps},
     * and one of the parents of the {@link AbstractSteps} class is implementing the {@link IFluentAdapter} interface.
     * There is a {@link IFluentAdapter#releaseFluent()} method which has to be called before/after
     * {@link WebDriver#quit()} method in order to properly release/close/quit the {@link WebDriver}.
     * <p>
     * We need to invoke the {@link IFluentAdapter#releaseFluent()} method because we are using {@link org.fluentlenium}
     * in this project.
     * <p>
     * One option is to hook it up here and the other option is to add {@link After} hook in all the *Steps classes
     * in {@link bdd.pk.lucidxpo.ynami.steps}.
     * <p>
     * We can invoke the {@link ApplicationContext#getBeansOfType(Class)} with either {@link AbstractSteps} or
     * {@link IFluentAdapter} which will result in same set of classes (i.e. a {@link Collection} containing
     * all out *Steps classes.
     */
    private Collection<IFluentAdapter> getFluentAdapters() {
        final Map<String, IFluentAdapter> fluentAdapters = this.applicationContext.getBeansOfType(IFluentAdapter.class);
        return fluentAdapters.values();
    }
}
