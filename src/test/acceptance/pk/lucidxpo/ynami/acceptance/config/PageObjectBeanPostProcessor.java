package pk.lucidxpo.ynami.acceptance.config;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import pk.lucidxpo.ynami.acceptance.pageobjects.PageObject;

import static org.openqa.selenium.support.PageFactory.initElements;

/*
 * This class is responsible for making Selenium initialize the page beans, i.e. to proxy all WebElement fields in them.
 */
@SuppressWarnings("NullableProblems")
@Component
class PageObjectBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    protected WebDriver webDriver;

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(PageObject.class)) {
            initElements(webDriver, bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
}