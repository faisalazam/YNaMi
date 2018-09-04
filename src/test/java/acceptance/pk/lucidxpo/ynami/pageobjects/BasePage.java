package acceptance.pk.lucidxpo.ynami.pageobjects;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;

import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

abstract class BasePage<T extends BasePage> extends FluentPage {

    private static final int REFRESH_RATE = 2;
    private static final int LOAD_TIMEOUT = 30;
    private static final String BASE_URL = "https://localhost:%s/ynami";

    /**
     * Provides condition when page can be considered as fully loaded.
     */
    protected abstract ExpectedCondition getPageLoadCondition();

    /**
     * Provides page relative URL/
     */
    protected abstract String getPageUrl();

    @SuppressWarnings("unchecked")
    public T openPage(final int port) {
        goTo(getBaseUrl(port) + getPageUrl());
        final ExpectedCondition pageLoadCondition = getPageLoadCondition();
        waitForPageToLoad(pageLoadCondition);
        return (T) this;
    }

    private String getBaseUrl(final int port) {
        return format(BASE_URL, port);
    }

    String pageTitle() {
        return getDriver().getTitle();
    }

    void editText(final String id, final String value) {
        final FluentWait<WebDriver> fluentWait = new FluentWait<>(getDriver());
        final WebElement element = fluentWait
                .withTimeout(ofSeconds(10))
                .pollingEvery(ofMillis(10))
                .ignoring(NoSuchElementException.class)
                .until(visibilityOfElementLocated(id(id)));
        element.sendKeys(value);
    }

    void clickId(final String id) {
        final WebElement button = getDriver().findElement(id(id));
        final JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", button);
    }

    @SuppressWarnings("unchecked")
    void waitForPageToLoad(final ExpectedCondition pageLoadCondition) {
        final Wait wait = new FluentWait(getDriver())
                .withTimeout(ofSeconds(LOAD_TIMEOUT))
                .pollingEvery(ofSeconds(REFRESH_RATE));

        wait.until(pageLoadCondition);
    }

    void navigate(final String value) {
        getDriver().navigate().to(value);
    }

    void acceptAlert() {
        final FluentWait<WebDriver> fluentWait = new FluentWait<>(getDriver());
        fluentWait.withTimeout(ofSeconds(10))
                .pollingEvery(ofMillis(10))
                .ignoring(NoSuchElementException.class)
                .until(alertIsPresent());
        getDriver().switchTo().alert().accept();
    }

    boolean isThere(final String name) {
        final List<WebElement> listTitles = getDriver()
                .findElements(xpath("//h2[contains(text(), ' " + name + " ')]"));
        return listTitles.size() == 1;
    }

    void clickXpathJs(final String value) {
        final WebElement button = getDriver()
                .findElement(xpath("//a[contains(text(), '" + value + "')]"));
        final JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", button);
    }

    protected void clickXpath(final String value) {
        getDriver().findElement(xpath("//*[contains(text(), '" + value + "')]")).click();
    }

    boolean hasErrors() {
        final List<WebElement> errors = getDriver()
                .findElements(className("error"));
        return (errors.size() > 0) && errors.get(0).isDisplayed();
    }
}