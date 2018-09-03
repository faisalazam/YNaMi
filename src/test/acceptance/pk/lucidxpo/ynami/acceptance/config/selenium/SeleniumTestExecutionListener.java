package pk.lucidxpo.ynami.acceptance.config.selenium;

import org.openqa.selenium.WebDriver;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static java.util.Objects.requireNonNull;

/*
 * This listener has two responsibilities: resetting the test scope before each test
 * and closing the Selenium driver, i.e. the browser, after each test.
 */
class SeleniumTestExecutionListener extends AbstractTestExecutionListener {
    private static final String WEB_DRIVER_BEAN_NAME = "webDriver";

    @Override
    public void beforeTestClass(final TestContext testContext) {
        reset(testContext);
    }

    @Override
    public void afterTestMethod(final TestContext testContext) {
        final TestScope testScope = getTestScopeBean(testContext);
        if (testScope.contains(WEB_DRIVER_BEAN_NAME)) {
            ((WebDriver) requireNonNull(testScope.remove(WEB_DRIVER_BEAN_NAME))).quit();
        }
    }

    @Override
    public void afterTestClass(final TestContext testContext) {
        reset(testContext);
    }

    private void reset(final TestContext testContext) {
        getTestScopeBean(testContext).reset();
    }

    private TestScope getTestScopeBean(final TestContext testContext) {
        return testContext.getApplicationContext().getBean(TestScope.class);
    }
}