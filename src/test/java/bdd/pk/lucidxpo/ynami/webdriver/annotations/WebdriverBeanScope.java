package bdd.pk.lucidxpo.ynami.webdriver.annotations;

import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static bdd.pk.lucidxpo.ynami.webdriver.annotations.WebdriverBeanScope.WEB_DRIVER_SCOPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation will be used to annotate the {@link WebDriver} beans. This is mainly created for parallel tests'
 * execution, i.e {@link WebdriverBeanScope} is for parallel test execution.
 */
@Bean
@Documented
@Target({METHOD})
@Retention(RUNTIME)
@Scope(WEB_DRIVER_SCOPE)
public @interface WebdriverBeanScope {
    String WEB_DRIVER_SCOPE = "webDriverScope";
}
