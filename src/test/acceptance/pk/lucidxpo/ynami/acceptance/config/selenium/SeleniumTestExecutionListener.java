package pk.lucidxpo.ynami.acceptance.config.selenium;

import org.openqa.selenium.WebDriver;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.requireNonNull;
import static org.springframework.test.annotation.DirtiesContext.HierarchyMode.EXHAUSTIVE;
import static org.springframework.test.context.support.DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE;

/*
 * This listener has two responsibilities: resetting the test scope before each test
 * and closing the Selenium driver, i.e. the browser, after each test.
 */
class SeleniumTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void prepareTestInstance(final TestContext testContext) {
        reset(testContext);
        testContext.markApplicationContextDirty(EXHAUSTIVE);
        testContext.setAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE, TRUE);
    }

    @Override
    public void afterTestMethod(final TestContext testContext) {
        final TestScope testScope = getTestScopeBean(testContext);
        if (testScope.contains("webDriver")) {
            ((WebDriver) requireNonNull(testScope.remove("webDriver"))).quit();
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