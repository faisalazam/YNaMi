package penetration.pk.lucidxpo.ynami.steps.defs;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.continuumsecurity.proxy.ContextModifier;
import net.continuumsecurity.proxy.Spider;
import net.continuumsecurity.proxy.ZAProxyScanner;
import org.zaproxy.clientapi.core.Alert;
import penetration.pk.lucidxpo.ynami.behaviours.INavigable;
import penetration.pk.lucidxpo.ynami.config.Config;
import penetration.pk.lucidxpo.ynami.exceptions.UnexpectedContentException;
import penetration.pk.lucidxpo.ynami.steps.domain.ZAPFalsePositive;
import penetration.pk.lucidxpo.ynami.web.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Thread.sleep;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zaproxy.clientapi.core.Alert.Risk;
import static org.zaproxy.clientapi.core.Alert.Risk.High;
import static org.zaproxy.clientapi.core.Alert.Risk.Low;
import static org.zaproxy.clientapi.core.Alert.Risk.Medium;
import static penetration.pk.lucidxpo.ynami.featureworld.ScenariosAwareWorld.getInstance;

@Slf4j
public class AppScanningSteps {
    private final Application app;
    private ZAProxyScanner scanner;
    private String scannerIds = null;
    private List<Alert> alerts = newArrayList();
    private final static String ZAP_CONTEXT_NAME = "Default Context";

    public AppScanningSteps() {
        app = Config.getInstance().createApp();
        // TODO there were some given, when, then which had been moved out of the constructor after removing
        //  cucumber-java8, check their impact.
    }

    @Given("the passive scanner has already run during the app navigation")
    public void thePassiveScannerHasAlreadyRunDuringTheAppNavigation() {
    }

    @Given("a new scanning session")
    public void aNewScanningSession() {
        app.enableHttpLoggingClient();
    }

    @Given("all existing alerts are deleted")
    public void allExistingAlertsAreDeleted() {
        this.clearAlerts();
    }

    @Given("a scanner with all policies disabled")
    public void aScannerWithAllPoliciesDisabled() {
        getScanner().disableAllScanners();
    }

    @When("the XML report is written to the file (.*)")
    public void theXmlReportIsWrittenToTheFile(final String path) throws Exception {
        writeReport(path, scanner.getXmlReport());
    }

    @Then("the HTML report is written to the file (.*)")
    public void theHtmlReportIsWrittenToTheFile(final String path) throws Exception {
        writeReport(path, scanner.getHtmlReport());
    }

    @Given("a scanner with all policies enabled")
    public void aScannerWithAllPoliciesEnabled() {
        getScanner().enableAllScanners();
    }

    @Given("the passive scanner is enabled")
    public void thePassiveScannerIsEnabled() {
        getScanner().setEnablePassiveScan(true);
    }

    @Given("the (\\S+) policy is enabled")
    public void thePolicyIsEnabled(final String policy) {
        this.enableScanners(policy);
    }

    @Given("the attack strength is set to (\\S+)")
    public void theAttackStrengthIsSetTo(final String strength) {
        this.setScannerAttackStrength(strength);
    }


    @Given("the alert threshold is set to (\\S+)")
    public void theAlertThresholdIsSetTo(final String threshold) {
        this.setScannerAlertThreshold(threshold);
    }

    @DataTableType
    @Given("the following URL regular expressions are excluded from the scanner")
    public String theFollowingURLRegularExpressionsAreExcludedFromTheScanner(final List<String> excludedRegexes) {
        // TODO: I don't know what should be the return type. Need to look into it after the DataTableType upgrade
        // and return something accordingly.
        this.excludeFromScanner(excludedRegexes);
        return "";
    }


    @When("the scanner is run")
    public void theScannerIsRun() throws InterruptedException {
        this.scan();
    }

    @Then("no (\\S+) or higher risk vulnerabilities should be present")
    public void noOrHigherRiskVulnerabilitiesShouldBePresent$(final String risk) {
        this.assertNoHigherRiskVulnerabilitiesPresent(risk);
    }

    @And("the navigation and spider status is reset")
    public void theNavigationAndSpiderStatusIsReset() {
        this.resetStatus();
    }

    @And("the application is navigated")
    public void theApplicationIsNavigated$() {
        this.navigateApplication();
    }

    @And("the application is spidered")
    public void theApplicationIsSpidered() {
        this.spiderApplication();
    }

    @When("the following false positives are removed")
    public void removeFalsePositives(final List<ZAPFalsePositive> falsePositives) {
        alerts = getScanner().getAlerts();
        final List<Alert> validFindings = newArrayList(alerts);
        alerts.forEach(alert -> falsePositives.stream()
                .filter(zapFalsePositive -> zapFalsePositive.matches(alert.getUrl(), alert.getParam(), alert.getCweId(), alert.getWascId()))
                .map(zapFalsePositive -> alert)
                .forEach(validFindings::remove)
        );
        alerts = validFindings;
    }

    private void writeReport(final String path, final byte[] xmlReport) throws IOException {
        final Path pathToFile = get(path);
        createDirectories(pathToFile.getParent());
        write(pathToFile, xmlReport);
    }

    private void clearAlerts() {
        getScanner().deleteAlerts();
        alerts.clear();
    }

    private void spiderApplication() {
        if (!getInstance().isSpidered()) {
            Config.getInstance().getIgnoreUrls().forEach(regex -> getSpider().excludeFromSpider(regex));
            try {
                getContext().setIncludeInContext(ZAP_CONTEXT_NAME, ".*"); //if URLs are not in context then they won't be spidered
            } catch (final Exception e) {
                e.printStackTrace();
            }
            final int maxDepth = Config.getInstance().getMaxDepth();
            getSpider().setMaxDepth(maxDepth);
            getSpider().setThreadCount(10);
            for (String url : Config.getInstance().getSpiderUrls()) {
                if (url.equalsIgnoreCase("baseurl")) {
                    url = Config.getInstance().getBaseUrl();
                }
                try {
                    spider(url);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waitForSpiderToComplete();
            getInstance().setSpidered(true);
        }
    }

    private void navigateApplication() {
        if (!getInstance().isNavigated()) {
            if (!(app instanceof INavigable)) {
                throw new RuntimeException("The application must implement the 'INavigable' interface to be navigable");
            }
            app.enableHttpLoggingClient();
            log.debug("Navigating");
            ((INavigable) app).navigate();
            getInstance().setNavigated(true);
        }
    }

    private void resetStatus() {
        getInstance().setNavigated(false);
        getInstance().setSpidered(false);
    }

    private void assertNoHigherRiskVulnerabilitiesPresent(final String risk) {
        final List<Alert> filteredAlerts;
        Risk riskLevel = High;

        if ("HIGH".equalsIgnoreCase(risk)) {
            riskLevel = High;
        } else if ("MEDIUM".equalsIgnoreCase(risk)) {
            riskLevel = Medium;
        } else if ("LOW".equalsIgnoreCase(risk)) {
            riskLevel = Low;
        }
        filteredAlerts = getAllAlertsByRiskRating(alerts, riskLevel);
        final String details = getAlertDetails(filteredAlerts);

        assertThat(filteredAlerts.size() + " " + risk + " vulnerabilities found.\nDetails:\n" + details, filteredAlerts.size(),
                equalTo(0));
    }

    private void scan() throws InterruptedException {
        log.info("Scanning: " + Config.getInstance().getBaseUrl());
        getScanner().scan(Config.getInstance().getBaseUrl());
        int complete = 0;
        final int scanId = getScanner().getLastScannerScanId();
        while (complete < 100) {
            complete = getScanner().getScanProgress(scanId);
            log.debug("Scan is " + complete + "% complete.");
            sleep(1000);
        }
    }

    private void excludeFromScanner(final List<String> excludedRegexes) {
        excludedRegexes.forEach(excluded -> getScanner().excludeFromScanner(excluded));
    }

    private void setScannerAlertThreshold(final String threshold) {
        if (scannerIds == null) {
            throw new RuntimeException("First set the scanning policy before setting attack strength or alert threshold");
        }
        stream(scannerIds.split(",")).forEach(id -> getScanner().setScannerAlertThreshold(id, threshold.toUpperCase()));
    }

    private void setScannerAttackStrength(final String strength) {
        if (scannerIds == null) {
            throw new RuntimeException("First set the scanning policy before setting attack strength or alert threshold");
        }
        stream(scannerIds.split(",")).forEach(id -> getScanner().setScannerAttackStrength(id, strength.toUpperCase()));
    }

    private void enableScanners(final String policyName) {
        switch (policyName.toLowerCase()) {
            case "directory-browsing":
                scannerIds = "0";
                break;
            case "cross-site-scripting":
                scannerIds = "40012,40014,40016,40017";
                break;
            case "sql-injection":
                scannerIds = "40018";
                break;
            case "path-traversal":
                scannerIds = "6";
                break;
            case "remote-file-inclusion":
                scannerIds = "7";
                break;
            case "server-side-include":
                scannerIds = "40009";
                break;
            case "script-active-scan-rules":
                scannerIds = "50000";
                break;
            case "server-side-code-injection":
                scannerIds = "90019";
                break;
            case "remote-os-command-injection":
                scannerIds = "90020";
                break;
            case "external-redirect":
                scannerIds = "20019";
                break;
            case "crlf-injection":
                scannerIds = "40003";
                break;
            case "source-code-disclosure":
                scannerIds = "42,10045,20017";
                break;
            case "shell-shock":
                scannerIds = "10048";
                break;
            case "remote-code-execution":
                scannerIds = "20018";
                break;
            case "ldap-injection":
                scannerIds = "40015";
                break;
            case "xpath-injection":
                scannerIds = "90021";
                break;
            case "xml-external-entity":
                scannerIds = "90023";
                break;
            case "padding-oracle":
                scannerIds = "90024";
                break;
            case "el-injection":
                scannerIds = "90025";
                break;
            case "insecure-http-methods":
                scannerIds = "90028";
                break;
            case "parameter-pollution":
                scannerIds = "20014";
                break;
            default:
                throw new RuntimeException("No policy found for: " + policyName);

        }
        if (scannerIds == null) {
            throw new UnexpectedContentException("No matching policy found for: " + policyName);
        }
        getScanner().setEnableScanners(scannerIds, true);
    }

    private ZAProxyScanner getScanner() {
        if (scanner == null) {
            final Config instance = Config.getInstance();
            scanner = new ZAProxyScanner(instance.getProxyHost(), instance.getProxyPort(), instance.getProxyApi());
            scanner.setAttackMode();
        }
        return scanner;
    }

    private Spider getSpider() {
        return getScanner();
    }

    private ContextModifier getContext() {
        return getScanner();
    }

    private void spider(final String url) throws InterruptedException {
        getSpider().spider(url, true, ZAP_CONTEXT_NAME);
        final int scanId = getSpider().getLastSpiderScanId();
        int complete = getSpider().getSpiderProgress(scanId);
        while (complete < 100) {
            complete = getSpider().getSpiderProgress(scanId);
            log.debug("Spidering of: " + url + " is " + complete + "% complete.");
            sleep(2000);
        }
        getSpider().getSpiderResults(scanId).stream().map(result -> "Found Url: " + result).forEach(log::debug);
    }

    private void waitForSpiderToComplete() {
        int status = 0;
        int counter99 = 0; //hack to detect a ZAP spider that gets stuck on 99%
        final int scanId = getSpider().getLastSpiderScanId();
        while (status < 100) {
            status = getSpider().getSpiderProgress(scanId);
            if (status == 99) {
                counter99++;
            }
            if (counter99 > 10) {
                break;
            }
            try {
                sleep(2000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Alert> getAllAlertsByRiskRating(final List<Alert> alerts, final Risk rating) {
        return alerts.stream().filter(alert -> alert.getRisk().ordinal() >= rating.ordinal()).collect(toList());
    }

    private String getAlertDetails(final List<Alert> alerts) {
        String detail = "";
        if (alerts.size() != 0) {
            detail = alerts.stream()
                    .map(alert -> alert.getName()
                            + "\n" + "URL: " + alert.getUrl()
                            + "\n" + "Parameter: " + alert.getParam()
                            + "\n" + "CWE-ID: " + alert.getCweId()
                            + "\n" + "WASC-ID: " + alert.getWascId()
                            + "\n"
                    ).collect(joining());
        }
        return detail;
    }

    private boolean alertsMatchByValue(final Alert first, final Alert second) {
        //The built in Alert.matches(Alert) method includes risk, reliability and alert, but not cweid.
        if (first.getCweId() != second.getCweId()) {
            return false;
        }
        if (!first.getParam().equals(second.getParam())) {
            return false;
        }
        if (!first.getUrl().equals(second.getUrl())) {
            return false;
        }
        if (!first.matches(second)) {
            return false;
        }
        return true;
    }
}