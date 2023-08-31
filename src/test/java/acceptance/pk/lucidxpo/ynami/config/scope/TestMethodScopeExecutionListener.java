package acceptance.pk.lucidxpo.ynami.config.scope;

import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static java.util.Objects.requireNonNull;

/**
 * This listener has two responsibilities: resetting the test scope before each test and closing the Selenium driver,
 * i.e. the browser, after each test.
 * <p>
 * That means the beans annotated with {@link TestMethodScopeBean} (i.e. {@link WebDriver} in this case) will be
 * removed from the {@link ApplicationContext} and {@link TestMethodScope}, as well as {@link WebDriver} will be quit.
 * So, each Selenium test will have its own {@link WebDriver} instance.
 * <p>
 * All that is achieved with the help of {@link TestMethodScopeBean}, {@link TestMethodScope} and
 * {@link TestMethodScopeExecutionListener} classes.
 * <p>
 * {@link TestMethodScopeBean} will mark the beans with {@link TestMethodScopeBean#TEST_METHOD_SCOPE},
 * {@link TestMethodScope} will act as a sort of repository for all the beans annotated with {@link TestMethodScopeBean},
 * and finally the {@link TestMethodScopeExecutionListener} will be clearing those beans based on the lifecycle of the test.
 */
public class TestMethodScopeExecutionListener extends AbstractTestExecutionListener {
    private static final String WEB_DRIVER_BEAN_NAME = "webDriver";

    @Override
    public void beforeTestClass(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        reset(testContext);
    }

    @Override
    public void afterTestMethod(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        final TestMethodScope testMethodScope = getTestMethodScopeBean(testContext);
        if (testMethodScope.contains(WEB_DRIVER_BEAN_NAME)) {
            ((WebDriver) requireNonNull(testMethodScope.remove(WEB_DRIVER_BEAN_NAME))).quit();
        }
    }

    @Override
    public void afterTestClass(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        reset(testContext);
    }

    private void reset(final TestContext testContext) {
        getTestMethodScopeBean(testContext).reset();
    }

    private TestMethodScope getTestMethodScopeBean(final TestContext testContext) {
        return testContext.getApplicationContext().getBean(TestMethodScope.class);
    }
}