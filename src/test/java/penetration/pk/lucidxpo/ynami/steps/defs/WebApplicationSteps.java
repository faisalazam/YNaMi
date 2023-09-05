package penetration.pk.lucidxpo.ynami.steps.defs;

import edu.umass.cs.benchlab.har.HarCookie;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarRequest;
import edu.umass.cs.benchlab.har.HarResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.continuumsecurity.proxy.LoggingProxy;
import net.continuumsecurity.proxy.ZAProxyScanner;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import penetration.pk.lucidxpo.ynami.behaviours.ILogin;
import penetration.pk.lucidxpo.ynami.behaviours.ILogout;
import penetration.pk.lucidxpo.ynami.clients.AuthTokenManagerImpl;
import penetration.pk.lucidxpo.ynami.config.Config;
import penetration.pk.lucidxpo.ynami.exceptions.ConfigurationException;
import penetration.pk.lucidxpo.ynami.exceptions.StepException;
import penetration.pk.lucidxpo.ynami.exceptions.UnexpectedContentException;
import penetration.pk.lucidxpo.ynami.model.Credentials;
import penetration.pk.lucidxpo.ynami.model.UserPassCredentials;
import penetration.pk.lucidxpo.ynami.web.Application;
import penetration.pk.lucidxpo.ynami.web.WebApplication;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.openqa.selenium.By.xpath;
import static penetration.pk.lucidxpo.ynami.featureworld.ScenariosAwareWorld.getInstance;
import static penetration.pk.lucidxpo.ynami.utils.Constants.HSTS;
import static penetration.pk.lucidxpo.ynami.utils.Constants.XFRAMEOPTIONS;
import static penetration.pk.lucidxpo.ynami.utils.Constants.XXSSPROTECTION;
import static penetration.pk.lucidxpo.ynami.utils.Utils.getResponseHeaderValue;
import static penetration.pk.lucidxpo.ynami.utils.Utils.replaceCookies;
import static penetration.pk.lucidxpo.ynami.utils.Utils.responseContainsHeader;
import static penetration.pk.lucidxpo.ynami.utils.Utils.responseHeaderValueIsOneOf;

@Slf4j
public class WebApplicationSteps {
    private Application app;
    private String methodName;
    private LoggingProxy proxy;
    private WebElement currentElement;

    public WebApplicationSteps() {
        /*
         * This has to be called explicitly when using an examples table in order to
         * start with a fresh browser getInstance, because @BeforeScenario is only
         * called once for the whole scenario, not each example.
         */
        // TODO there were some given, when, then which had been moved out of the constructor after removing
        //  cucumber-java8, check their impact.
    }

    @Given("a new browser or client instance")
    public void aNewBrowserOrClientInstance() {
        this.createApp();
    }

    @And("the client\\/browser is configured to use an intercepting proxy")
    public void theClientBrowserIsConfiguredToUseAnInterceptingProxy() {
        this.enableLoggingDriver();
    }

    @And("the proxy logs are cleared")
    public void theProxyLogsAreCleared() {
        this.clearProxy();
    }

    @And("the login page")
    public void theLoginPage() {
        this.openLoginPage();
    }

    @And("the username (.*) is used")
    public void theUsernameIsUsed(final String username) {
        this.setUsername(username);
    }

    @And("the password (.*) is used")
    public void thePasswordIsUsed(final String password) {
        this.setPassword(password);
    }

    @When("the user logs in")
    public void theUserLogsI() {
        this.loginWithSetCredentials();
    }

    @And("the HTTP requests and responses are recorded")
    public void theHttpRequestsAndResponsesAreRecorded() {
        //HTTP traffic is recorded in checkAccessToResource
    }

    @And("they access the restricted resource: (.*)")
    public void theyAccessTheRestrictedResource(final String resource) {
        this.setMethodName(resource);
    }

    @Then("the string: (.*) should be present in one of the HTTP responses")
    public void theStringShouldBePresentInOneOfTheHTTPResponses(final String str) throws NoSuchMethodException {
        this.assertSensitiveDataPresentInResponses(str);
    }

    @Given("the access control map for authorised users has been populated")
    public void theAccessControlMapForAuthorisedUsersHasBeenPopulated() {
        if (getMethodProxyMap().isEmpty()) {
            throw new RuntimeException("Access control map has not been populated.");
        }
    }

    @And("the previously recorded HTTP Requests for (.*) are replayed using the current session ID")
    public void thePreviouslyRecordedHTTPRequestsForAreReplayedUsingTheCurrentSessionID(final String requests) {
        this.setMethodName(requests);
    }

    @Then("the string: (.*) should not be present in any of the HTTP responses")
    public void theStringShouldNotBePresentInAnyOfTheHTTPResponses(final String str) {
        this.assertSensitiveDataNotPresentInResponses(str);
    }

    @When("the default password")
    public void theDefaultPassword() {
        setPassword(((UserPassCredentials) Config.getInstance().getDefaultCredentials()).getPassword());
    }

    @When("the authentication tokens on the client are deleted")
    public void deleteAuthTokensOnClient() {
        deleteAuthTokens();
    }

    @Given("a new browser instance")
    public void createAppForBrowser() {
        createApp();
        if (!(app.getAuthTokenManager() instanceof AuthTokenManagerImpl)) {
            throw new ConfigurationException("This scenario can only be run with a Browser getInstance, but application.getAuthTokenManager() returns a non-browser client.");
        }
    }

    @When("the login page is displayed")
    public void displayLoginPage() {
        openLoginPage();
    }

    @When("the default user logs in")
    public void loginDefaultUser() {
        openLoginPage();
        setDefaultCredentials(Config.getInstance().getDefaultCredentials());
        loginWithSetCredentials();
    }

    @Given("an invalid username")
    public void setInvalidUsername() {
        setUsername(Config.getInstance().getIncorrectUsername());
    }

    @Given("an incorrect password")
    public void incorrectPassword() {
        setPassword(Config.getInstance().getIncorrectPassword());
    }

    @When("the user logs in from a fresh login page")
    public void loginFromFreshPage() {
        createApp();
        openLoginPage();
        loginWithSetCredentials();
    }

    @Then("the user is logged in")
    public void loginSucceeds() {
        assertThat("The user is logged in", ((ILogin) app).isLoggedIn(), is(true));
    }

    @Then("the user is not logged in")
    public void loginFails() {
        assertThat("The user is not logged in", ((ILogin) app).isLoggedIn(), is(false));
    }

    @When("the case of the password is changed")
    public void changeCaseOfPassword() {
        final UserPassCredentials credentials = getUserPassCredentials();
        String wrongCasePassword = credentials.getPassword().toUpperCase();

        if (wrongCasePassword.equals(credentials.getPassword())) {
            wrongCasePassword = credentials.getPassword().toLowerCase();
            if (wrongCasePassword.equals(credentials.getPassword())) {
                throw new RuntimeException(
                        "Password doesn't have alphabetic characters, can't run this test.");
            } else {
                credentials.setPassword(wrongCasePassword);
            }
        } else {
            credentials.setPassword(wrongCasePassword);
        }
    }

    @Given("the user logs in from a fresh login page\\ (\\d+) times")
    public void whenTheUserLogsInFromAFreshLoginPageXTimes(final int limit) {
        range(0, limit).forEach(i -> loginFromFreshPage());
    }

    @When("the user logs out")
    public void logout() {
        ((ILogout) app).logout();
    }

    @Given("the HTTP request-response containing the default credentials is selected")
    public void findRequestWithPassword() {
        final UserPassCredentials credentials = getUserPassCredentials();
        final List<HarEntry> requests = getProxy().findInRequestHistory(credentials.getPassword());
        if (requests == null || requests.isEmpty()) {
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        }
        setCurrentHar(requests.get(0));
    }

    @Given("the HTTP request containing the string (\\s+) is selected")
    public void findRequestWithString(final String value) {
        final UserPassCredentials credentials = getUserPassCredentials();
        final List<HarEntry> requests = getProxy().findInRequestHistory(value);
        if (requests == null || requests.isEmpty()) {
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        }
        setCurrentHar(requests.get(0));
    }

    @Then("the protocol should be HTTPS")
    public void verifyProtocolHttps() {
        final HarEntry currentHar = getCurrentHar();
        assertThat(currentHar.getRequest().getUrl(), currentHar.getRequest().getUrl().substring(0, 5), equalTo("https"));
    }

    @Given("the HTTP request-response containing the login form")
    public void findResponseWithLoginform() {
        final String regex = "(?i)input[\\s\\w=:'\\-\\\"]*type\\s*=\\s*['\\\"]password['\\\"]";
        final List<HarEntry> responses = getProxy().findInResponseHistory(regex);
        if (responses == null || responses.isEmpty()) {
            throw new StepException(
                    "Could not find HTTP response with password form using regex: "
                            + regex);
        }
        setCurrentHar(responses.get(0));
    }

    @Given("the first HTTP request-response stored by the proxy is selected")
    public void findFirstHar() {
        final List<HarEntry> responses = getProxy().getHistory();
        if (responses == null || responses.isEmpty()) {
            throw new StepException(
                    "No request-responses found");
        }
        setCurrentHar(responses.get(0));
    }

    @Then("the response status code should start with 3")
    public void statusCode3xx() {
        assertThat(Integer.toString(getCurrentHarResponse().getStatus()).substring(0, 1), equalTo("3"));
    }

    @Given("the value of the session ID is noted")
    public void findAndSetSessionIds() {
        getSessionIds().clear();
        getSessionIds().putAll(app.getAuthTokenManager().getAuthTokens());
    }

    @Then("the value of the session cookie issued after authentication should be different from that of the previously noted session ID")
    public void compareSessionIds() {
        getSessionIds().keySet().forEach(
                name -> assertThat(app.getAuthTokenManager().getAuthTokens().get(name), not(getSessionIds().get(name)))
        );
    }

    @Then("the session cookie should have the secure flag set")
    public void sessionCookiesSecureFlag() {
        Config.getInstance().getSessionIDs().forEach(
                name -> assertThat(((AuthTokenManagerImpl) app.getAuthTokenManager()).getCookieByName(name).isSecure(), equalTo(true))
        );
    }

    @Then("the session cookie should have the httpOnly flag set")
    public void sessionCookiesHttpOnlyFlag() {
        final Set<String> sessionIDs = Config.getInstance().getSessionIDs();
        final List<HarCookie> harCookies = getProxy().getHistory().stream()
                .flatMap(harEntry -> sessionIDs.stream()
                        .flatMap(sessionID -> harEntry.getResponse().getCookies().getCookies().stream()
                                .filter(cookie -> cookie.getName().equalsIgnoreCase(sessionID))
                        )
                ).collect(toList());
        assertFalse(harCookies.isEmpty());
        harCookies.forEach(harCookie -> {
            assertTrue(harCookie.isSecure(), harCookie.getName() + " cookie should have secure flag set");
            assertTrue(harCookie.isHttpOnly(), harCookie.getName() + " cookie should have httpOnly flag set");
        });
    }

    @When("the session is inactive for (\\d+) minutes")
    public void waitForTime(final int minutes) {
        try {
            sleep(minutes * 60 * 1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    @When("the password field is inspected")
    public void selectPasswordField() {
        checkIfWebApplication();
        final String xpath = "//input[@type='password']";
        final List<WebElement> passwords = ((WebApplication) app).getAuthTokenManagerImpl().getDriver().findElements(xpath(xpath));
        if (passwords.size() != 1) {
            throw new UnexpectedContentException("Found " + passwords.size() + " password fields using XPath: " + xpath);
        }
        currentElement = passwords.get(0);
    }

    @When("the login form is inspected")
    public void selectLoginFormElement() {
        checkIfWebApplication();
        final String xpath = "//form[.//input[@type='password']]";
        final List<WebElement> loginForms = ((WebApplication) app).getAuthTokenManagerImpl().getDriver().findElements(xpath(xpath));
        if (loginForms.size() != 1) {
            throw new UnexpectedContentException("Found " + loginForms.size() + " login forms using XPath: " + xpath);
        }
        currentElement = loginForms.get(0);
    }

    @Then("it should have the autocomplete attribute set to 'off'")
    public void thenTheLogonFormShouldHaveTheAutocompleteDirectiveSetToOff() {
        assertThat("Autocomplete set to off", currentElement.getAttribute("autocomplete"), equalToIgnoringCase("off"));
    }

    @Then("no exceptions are thrown")
    public void doNothing() {

    }

    @When("the response that contains the string: (.*) is recorded")
    public void recordSensitiveResponse(final String sensitiveData) {
        final UserPassCredentials credentials = getUserPassCredentials();
        try {
            app.getClass().getMethod(methodName).invoke(app);
            getInstance().setRecordedEntries(getProxy().findInResponseHistory(sensitiveData));
            assertThat("The string: " + sensitiveData + " was not found in the HTTP responses", getInstance().getRecordedEntries().size(), greaterThan(0));
            setCurrentHar(getInstance().getRecordedEntries().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + methodName + "()");

        }
    }

    @Then("the X-Frame-Options header is either (.*) or (.*)")
    public void checkIfXFrameOptionsHeaderIsSet(final String sameOrigin, final String deny) {
        final String xFrameOptionsValue = getResponseHeaderValue(getCurrentHarResponse(), XFRAMEOPTIONS);
        assertThat(XFRAMEOPTIONS + " header is not set", xFrameOptionsValue, notNullValue());
        assertThat("X-FRAME-Options header is: " + xFrameOptionsValue, responseHeaderValueIsOneOf(getCurrentHarResponse(), XFRAMEOPTIONS, new String[]{sameOrigin, deny}), equalTo(true));
    }

    @When("the first HTTP request-response is recorded")
    public void recordFirstHarEntry() {
        final List<HarEntry> history = getProxy().getHistory();
        if (history == null || history.isEmpty()) {
            throw new RuntimeException("No HTTP requests-responses recorded");
        }
        setCurrentHar(history.get(0));
    }

    @Then("the Strict-Transport-Security header is set")
    public void checkIfHSTSHeaderIsSet() {
        assertThat("No " + HSTS + " header found", responseContainsHeader(getCurrentHarResponse(), HSTS), equalTo(true));
    }

    @Then("the HTTP (.*) header has the value: (.*)")
    public void checkHeaderValue(final String name, final String value) {
        final HarEntry currentHar = getCurrentHar();
        assertNotNull("No HTTP header named: " + name + " was found.", getResponseHeaderValue(currentHar.getResponse(), name));
        assertThat(getResponseHeaderValue(currentHar.getResponse(), name), equalTo(value));
    }

    @Then("the Access-Control-Allow-Origin header must not be: (.*)")
    public void checkThatAccessControlAllowOriginIsNotStar(final String star) {
        assertThat(getResponseHeaderValue(getCurrentHarResponse(), XXSSPROTECTION), not(star));
    }

    @When("the following URLs are visited and their HTTP responses recorded")
    public void accessSecureBaseUrlAndRecordHTTPResponse(final List<String> urls) {
        for (String url : urls) {
            if (!getInstance().isHttpHeadersRecorded()) {
                enableLoggingDriver();
                clearProxy();
                if ("baseUrl".equalsIgnoreCase(url)) {
                    url = Config.getInstance().getBaseUrl();
                }
                ((AuthTokenManagerImpl) app.getAuthTokenManager()).getUrl(url);
                recordFirstHarEntry();
                getInstance().setHttpHeadersRecorded(true);
            }
        }
    }

    @And("the default username")
    public void theDefaultUsername() {
        final String username = ((UserPassCredentials) Config.getInstance().getDefaultCredentials()).getUsername();
        setUsername(username);
    }

    private void createApp() {
        app = Config.getInstance().createApp();
        app.enableDefaultClient();
        assert app.getAuthTokenManager() != null;
        app.getAuthTokenManager().deleteAuthTokens();
        getInstance().setCredentials(new UserPassCredentials(EMPTY, EMPTY));
    }

    private void deleteAuthTokens() {
        app.getAuthTokenManager().deleteAuthTokens();
    }

    private void setDefaultCredentials(final Credentials creds) {
        getInstance().setCredentials(creds);
    }

    private LoggingProxy getProxy() {
        if (proxy == null) {
            final Config instance = Config.getInstance();
            proxy = new ZAProxyScanner(instance.getProxyHost(), instance.getProxyPort(), instance.getProxyApi());
        }
        proxy.setAttackMode();
        return proxy;
    }

    private void openLoginPage() {
        ((ILogin) app).openLoginPage();
    }

    private HarResponse getCurrentHarResponse() {
        return getCurrentHar().getResponse();
    }

    private void loginWithSetCredentials() {
        assert getCredentials() != null;
        ((ILogin) app).login(getCredentials());
    }

    private void checkIfWebApplication() {
        if (!(app instanceof WebApplication)) {
            throw new RuntimeException("This scenario can only be run against a WebApplication");
        }
    }

    private void enableLoggingDriver() {
        app.enableHttpLoggingClient();
    }

    private void clearProxy() {
        getProxy().clear();
    }

    private void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    private void setPassword(final String password) {
        getUserPassCredentials().setPassword(password);
    }

    private void setUsername(final String username) {
        getUserPassCredentials().setUsername(username);
    }

    private HarEntry getCurrentHar() {
        return getInstance().getCurrentHar();
    }

    private void setCurrentHar(final HarEntry currentHar) {
        getInstance().setCurrentHar(currentHar);
    }

    private Map<String, List<HarEntry>> getMethodProxyMap() {
        return getInstance().getMethodProxyMap();
    }

    private Map<String, String> getSessionIds() {
        return getInstance().getSessionIds();
    }

    private Credentials getCredentials() {
        return getInstance().getCredentials();
    }

    private UserPassCredentials getUserPassCredentials() {
        return getInstance().getUserPassCredentials();
    }

    private void assertSensitiveDataNotPresentInResponses(final String sensitiveData) {
        final Map<String, List<HarEntry>> methodProxyMap = getMethodProxyMap();
        if (methodProxyMap == null || methodProxyMap.get(methodName).isEmpty()) {
            throw new ConfigurationException(
                    "No HTTP messages were recorded for the method: " + methodName);
        }
        findAndSetSessionIds();
        if (app instanceof WebApplication) {
            checkAccessUsingCookieMethod(sensitiveData);
        } else {
            checkAccessUsingAuthTokenMethod(sensitiveData);
        }
    }

    private void assertSensitiveDataPresentInResponses(final String sensitiveData) throws NoSuchMethodException {
        final UserPassCredentials credentials = getUserPassCredentials();
        try {
            app.getClass().getMethod(methodName).invoke(app);
            // For web services, calling the method might throw an exception if access is denied.
        } catch (final NoSuchMethodException nsm) {
            throw nsm;
        } catch (final Exception e) {
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + methodName + "()");
        }
        if (getMethodProxyMap().get(methodName) != null) {
            log.info("The method: "
                    + methodName
                    + " has already been added to the map, using the existing HTTP logs");
            return;
        }
        getMethodProxyMap().put(methodName, getProxy().getHistory());
        final boolean accessible = getProxy().findInResponseHistory(sensitiveData).size() > 0;
        if (accessible) {
            log.debug("User: " + credentials.getUsername() + " can access resource: " + methodName);
        }
        assertThat("User: " + credentials.getUsername() + " could access resource: " + methodName + " because the text: [" + sensitiveData + "] was present in the responses", accessible, equalTo(true));
    }

    private void checkAccessUsingAuthTokenMethod(final String sensitiveData) {
        final boolean accessible;
        app.getAuthTokenManager().deleteAuthTokens();
        app.getAuthTokenManager().setAuthTokens(getSessionIds());
        clearProxy();
        try {
            app.getClass().getMethod(methodName).invoke(app);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final List<HarEntry> results = getProxy().findInResponseHistory(sensitiveData);
        accessible = results != null && results.size() > 0;
        if (!accessible) {
            log.debug("User: " + getUserPassCredentials().getUsername() + " has no access to resource: " + methodName);
        }
        assertThat(accessible, equalTo(false));
    }

    private void checkAccessUsingCookieMethod(final String sensitiveData) {
        boolean accessible = false;
        final List<HarEntry> harEntries = getMethodProxyMap().get(methodName);
        for (final HarEntry entry : harEntries) {
            if (entry.getResponse().getBodySize() > 0) {
                clearProxy();
                final HarRequest manual;
                try {
                    manual = replaceCookies(entry.getRequest(), getSessionIds());
                } catch (final Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Could not copy Har request");
                }
                getProxy().makeRequest(manual, false); //TODO change this to true once ZAP bug is fixed
                final List<HarEntry> results = getProxy().findInResponseHistory(sensitiveData);
                accessible = results != null && results.size() > 0;
                if (accessible) {
                    break;
                }
            }
        }
        if (!accessible) {
            log.debug("User: " + getUserPassCredentials().getUsername() + " has no access to resource: " + methodName);
        }
        assertThat(accessible, equalTo(false));
    }

    public Application getWebApplication() {
        return app;
    }

    private Cookie findCookieByName(final List<Cookie> cookies, final String name) {
        if (cookies.isEmpty()) {
            return null;
        }
        for (final Cookie cookie : cookies) {
            if (cookie == null) {
                return null;
            }
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie;
            }
        }
        return null;
    }
}