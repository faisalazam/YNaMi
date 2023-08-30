package acceptance.pk.lucidxpo.ynami.cucumber.webdriver.scope;

import acceptance.pk.lucidxpo.ynami.cucumber.webdriver.annotations.WebdriverBeanScope;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static acceptance.pk.lucidxpo.ynami.cucumber.webdriver.annotations.WebdriverBeanScope.WEB_DRIVER_SCOPE;

/**
 * This class is responsible for registering the {@link WebdriverBeanScope#WEB_DRIVER_SCOPE} custom bean scope, which
 * has been add specifically for the management of {@link WebDriver}. The {@link WebdriverScope} is the one responsible
 * for returning the beans annotated with {@link WebdriverBeanScope}, which in this case, will be {@link WebDriver}.
 */
public class WebdriverScopePostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(WEB_DRIVER_SCOPE, new WebdriverScope());
    }
}
