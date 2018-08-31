package pk.lucidxpo.ynami.acceptance.config;

import org.openqa.selenium.WebDriver;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/*
 * This listener has two responsibilities: resetting the test scope before each test
 * and closing the Selenium driver, i.e. the browser, after each test.
 */
class SeleniumTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void prepareTestInstance(final TestContext testContext) {
        reset(testContext);
    }

    @Override
    public void afterTestMethod(final TestContext testContext) {
        testContext.getApplicationContext().getBean(WebDriver.class).quit();
        reset(testContext);
    }

    private void reset(final TestContext testContext) {
        testContext.getApplicationContext().getBean(TestScope.class).reset();
    }
}