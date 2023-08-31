package acceptance.pk.lucidxpo.ynami.selenium.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import static acceptance.pk.lucidxpo.ynami.selenium.config.TestMethodScopeBean.TEST_METHOD_SCOPE;

/**
 * In order to make the Spring container aware of the new {@link TestMethodScope} scope, it has to be registered
 * through the {@link ConfigurableListableBeanFactory#registerScope} method on a {@link ConfigurableBeanFactory}
 * instance. {@link TestMethodScopeBeanFactoryPostProcessor} is taken care of that.
 */
@Component
public class TestMethodScopeBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory factory) throws BeansException {
        final TestMethodScope testMethodScope = factory.getBean(TestMethodScope.class);
        // The first parameter, scopeName, is used to identify/specify a scope by its unique name.
        // The second parameter, scope, is an actual instance of the custom Scope implementation that we wish to
        // register and use.
        factory.registerScope(TEST_METHOD_SCOPE, testMethodScope);
    }
}
