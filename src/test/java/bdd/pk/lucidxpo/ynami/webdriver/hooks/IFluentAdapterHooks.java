package bdd.pk.lucidxpo.ynami.webdriver.hooks;

import bdd.pk.lucidxpo.ynami.annotations.LazyAutowired;
import bdd.pk.lucidxpo.ynami.config.AbstractSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.fluentlenium.adapter.IFluentAdapter;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Map;

/**
 * This class is responsible for managing the {@link org.fluentlenium} related hooks.
 */
public class IFluentAdapterHooks {
    @LazyAutowired
    private ApplicationContext applicationContext;

    /**
     * There is a {@link IFluentAdapter#initFluent(WebDriver)} method which has to be hooked with {@link Before} in
     * order to init fluent with {@link WebDriver}.
     * <p>
     * One option is to hook it up here and the other option is to add {@link Before} hook in all the *Steps classes
     * in {@link bdd.pk.lucidxpo.ynami.steps}.
     */
    @Before
    public void before() {
        final WebDriver webDriver = this.applicationContext.getBean(WebDriver.class);
        getFluentAdapters().forEach(iFluentAdapter -> iFluentAdapter.initFluent(webDriver));
    }

    /**
     * There is a {@link IFluentAdapter#releaseFluent()} method which has to be called before/after
     * {@link WebDriver#quit()} method in order to properly release/close/quit the {@link WebDriver}.
     * <p>
     * We need to invoke the {@link IFluentAdapter#releaseFluent()} method because we are using {@link org.fluentlenium}
     * in this project.
     * <p>
     * One option is to hook it up here and the other option is to add {@link After} hook in all the *Steps classes
     * in {@link bdd.pk.lucidxpo.ynami.steps}.
     */
    @After
    public void afterScenario() {
        getFluentAdapters().forEach(IFluentAdapter::releaseFluent);
    }

    /**
     * All of the *Steps classes (in {@link bdd.pk.lucidxpo.ynami.steps}) are extended from {@link AbstractSteps},
     * and one of the parents of the {@link AbstractSteps} class is implementing the {@link IFluentAdapter} interface.
     * <p>
     * We can invoke the {@link ApplicationContext#getBeansOfType(Class)} with either {@link AbstractSteps} or
     * {@link IFluentAdapter} which will result in same set of classes (i.e. a {@link Collection} containing
     * all out *Steps classes.
     * <p>
     * This {@link Collection<IFluentAdapter>} will be used later to invoke {@link IFluentAdapter#initFluent(WebDriver)}
     * and {@link IFluentAdapter#releaseFluent()} methods.
     */
    private Collection<IFluentAdapter> getFluentAdapters() {
        final Map<String, IFluentAdapter> fluentAdapters = this.applicationContext.getBeansOfType(IFluentAdapter.class);
        return fluentAdapters.values();
    }
}
