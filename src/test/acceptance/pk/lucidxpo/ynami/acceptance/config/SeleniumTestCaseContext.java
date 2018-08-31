package pk.lucidxpo.ynami.acceptance.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumTestCaseContext implements BeanFactoryPostProcessor {

    private static final String TEST_SCOPE_NAME = "test";

    @Bean
    public TestScope testScope() {
        return new TestScope();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope(TEST_SCOPE_NAME, testScope());
    }
}