package pk.lucidxpo.ynami.utils.ui.pageobjects;

import io.fluentlenium.core.FluentPage;
import org.assertj.core.api.AbstractAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.junit.platform.commons.util.ExceptionUtils.throwAsUncheckedException;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;

abstract class BasePage<
        PageObject extends BasePage<PageObject, PageAssert>,
        PageAssert extends AbstractAssert<PageAssert, PageObject>
        > extends FluentPage {

    private static final int REFRESH_RATE = 2;
    private static final int LOAD_TIMEOUT = 30;
    private static final String BASE_URL = "https://localhost:%s/ynami";

    /**
     * Provides condition when page can be considered as fully loaded.
     */
    protected abstract ExpectedCondition<?> getPageLoadCondition();

    @SuppressWarnings("unchecked")
    public PageObject openPage(final int port) {
        goTo(getBaseUrl(port) + getUrl());
        final ExpectedCondition<?> pageLoadCondition = getPageLoadCondition();
        waitForPageToLoad(pageLoadCondition);
        return (PageObject) this;
    }

    @SuppressWarnings("unchecked")
    public PageAssert assertThat() {
        try {
            final Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
            final Class<PageObject> pageObjectClass = (Class<PageObject>) actualTypeArguments[0];
            final Class<PageAssert> pageAssertClass = (Class<PageAssert>) actualTypeArguments[1];
            return pageAssertClass.getDeclaredConstructor(pageObjectClass).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | SecurityException | NoSuchMethodException e) {
            throwAsUncheckedException(e);
        }
        return null;
    }

    private String getBaseUrl(final int port) {
        return format(BASE_URL, port);
    }

    @SuppressWarnings("unused")
    String pageTitle() {
        return getDriver().getTitle();
    }

    void clearAndFill(final String id, final String value) {
        final WebElement element = getWebElementWithFluentWait(id);
        element.clear();
        element.sendKeys(value);
    }

    void editText(final String id, final String value) {
        writeText(id(id), value);
    }

    //Write Text by using JAVA Generics (You can use both By or WebElement)
    <T> void writeText(T elementAttr, String text) {
        waitElement(elementAttr);
        if (isBy(elementAttr)) {
            getFluentWait().until(presenceOfAllElementsLocatedBy((By) elementAttr));
            getDriver()
                    .findElement((By) elementAttr)
                    .sendKeys(text);
        } else {
            getFluentWait().until(visibilityOf((WebElement) elementAttr));
            ((WebElement) elementAttr).sendKeys(text);
        }
    }

    //Read Text by using JAVA Generics (You can use both By or WebElement)
    @SuppressWarnings("unused")
    <T> String readText(T elementAttr) {
        if (isBy(elementAttr)) {
            return getDriver()
                    .findElement((By) elementAttr)
                    .getText();
        } else {
            return ((WebElement) elementAttr).getText();
        }
    }

    void clickId(@SuppressWarnings("SameParameterValue") final String id) {
        final WebElement button = getDriver().findElement(id(id));
        final JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", button);
    }

    public void waitForPageToLoad(final ExpectedCondition<?> pageLoadCondition) {
        final Wait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(ofSeconds(LOAD_TIMEOUT))
                .pollingEvery(ofSeconds(REFRESH_RATE));

        wait.until(pageLoadCondition);
    }

    @SuppressWarnings("unused")
    void navigate(final String value) {
        getDriver().navigate().to(value);
    }

    @SuppressWarnings("unused")
    void acceptAlert() {
        fluentWaitIgnoringNoSuchElementException().until(alertIsPresent());
        getDriver().switchTo().alert().accept();
    }

    @SuppressWarnings("unused")
    boolean isThere(final String name) {
        final List<WebElement> listTitles = getDriver()
                .findElements(xpath("//h2[contains(text(), ' " + name + " ')]"));
        return listTitles.size() == 1;
    }

    @SuppressWarnings("SameParameterValue")
    void clickXpathJs(final String value) {
        final WebElement button = getDriver()
                .findElement(xpath("//a[contains(text(), '" + value + "')]"));
        final JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", button);
    }

    @SuppressWarnings("unused")
    protected void clickXpath(final String value) {
        getDriver().findElement(xpath("//*[contains(text(), '" + value + "')]")).click();
    }

    @SuppressWarnings("unused")
    boolean hasErrors() {
        final List<WebElement> errors = getDriver()
                .findElements(className("error"));
        return (errors.size() > 0) && errors.get(0).isDisplayed();
    }

    <T> WebElement waitElement(T elementAttr) {
        if (isBy(elementAttr)) {
            return getFluentWait().until(presenceOfElementLocated((By) elementAttr));
        } else {
            return getFluentWait().until(visibilityOf((WebElement) elementAttr));
        }
    }

    @SuppressWarnings("unused")
    <T> void waitElements(T elementAttr) {
        if (isBy(elementAttr)) {
            getFluentWait().until(presenceOfAllElementsLocatedBy((By) elementAttr));
        } else {
            getFluentWait().until(visibilityOfAllElements((WebElement) elementAttr));
        }
    }

    private static <T> boolean isBy(T elementAttr) {
        return elementAttr
                .getClass()
                .getName()
                .contains("By");
    }

    private WebElement getWebElementWithFluentWait(String id) {
        return waitElement(id(id));
    }

    private FluentWait<WebDriver> fluentWaitIgnoringNoSuchElementException() {
        return getFluentWait().ignoring(NoSuchElementException.class);
    }

    // TODO: Try to make it a bean in WebDriverWaitConfig and autowire it here somehow
    private FluentWait<WebDriver> getFluentWait() {
        return new FluentWait<>(getDriver())
                .withTimeout(ofSeconds(10))
                .pollingEvery(ofMillis(10));
    }
}