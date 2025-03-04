package bdd.pk.lucidxpo.ynami.webdriver.scope;

import bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope.WEB_DRIVER_SCOPE;

/**
 * This class is responsible for registering the {@link WebdriverBeanScope#WEB_DRIVER_SCOPE} custom bean scope, which
 * has been added specifically for the management of {@link WebDriver}. The {@link WebdriverScope} is the one responsible
 * for returning the beans annotated with {@link WebdriverBeanScope}, which in this case, will be {@link WebDriver}.
 */
public class WebdriverScopePostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // The first parameter, scopeName, is used to identify/specify a scope by its unique name.
        // The second parameter, scope, is an actual instance of the custom Scope implementation that we wish to
        // register and use.
        beanFactory.registerScope(WEB_DRIVER_SCOPE, new WebdriverScope());
    }
}
