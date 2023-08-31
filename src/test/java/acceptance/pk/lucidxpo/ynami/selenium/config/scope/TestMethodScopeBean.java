package acceptance.pk.lucidxpo.ynami.selenium.config.scope;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static acceptance.pk.lucidxpo.ynami.selenium.config.scope.TestMethodScopeBean.TEST_METHOD_SCOPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation will be used to annotate the beans which should last just for the life of the {@link Test} method,
 * and in this case, particularly {@link WebDriver} beans.
 * <p>
 * That means the beans annotated with {@link TestMethodScopeBean} (i.e. {@link WebDriver} in this case) will be
 * removed from the {@link ApplicationContext} and {@link TestMethodScope}, as well as {@link WebDriver} will be quit.
 * So, each Selenium test will have its own {@link WebDriver} instance.
 * <p>
 * All that is achieved with the help of {@link TestMethodScopeBean}, {@link TestMethodScope} and
 * {@link SeleniumTestExecutionListener} classes.
 * <p>
 * {@link TestMethodScopeBean} will mark the beans with {@link TestMethodScopeBean#TEST_METHOD_SCOPE},
 * {@link TestMethodScope} will act as a sort of repository for all the beans annotated with {@link TestMethodScopeBean},
 * and finally the {@link SeleniumTestExecutionListener} will be clearing those beans based on the lifecycle of the test.
 */
@Bean
@Documented
@Target({METHOD})
@Retention(RUNTIME)
@Scope(TEST_METHOD_SCOPE)
public @interface TestMethodScopeBean {
    String TEST_METHOD_SCOPE = "testMethodScope";
}
