package penetration.pk.lucidxpo.ynami.steps.defs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java8.En;
import net.continuumsecurity.ReportClient;
import net.continuumsecurity.ScanClient;
import net.continuumsecurity.v5.model.Issue;
import penetration.pk.lucidxpo.ynami.steps.domain.NessusFalsePositive;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;
import static net.continuumsecurity.ClientFactory.createReportClient;
import static net.continuumsecurity.ClientFactory.createScanClient;
import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static penetration.pk.lucidxpo.ynami.config.Config.getInstance;

public class NessusScanningSteps implements En {
    private String username;
    private String password;
    private String scanUuid;
    private String nessusUrl;
    private int nessusVersion;
    private String policyName;
    private ScanClient scanClient;
    private Map<Integer, Issue> issues;
    private String scanIdentifierForStatus;
    private boolean ignoreHostNamesInSSLCert = false;
    private final List<String> hostNames = newArrayList();

//    TODO: try to use scenarioworld instead of maintaining steps awareness in this class

    public NessusScanningSteps() {
        Given("a nessus API client that accepts all hostnames in SSL certificates", () -> ignoreHostNamesInSSLCert = true);

        Given("the nessus username (.*) and the password (.*)$", (final String username, final String password) -> {
            this.username = username;
            this.password = password;
        });

        Given("the scanning policy named (.*)$", (final String policyName) -> this.policyName = policyName);

        When("^the scanner is run with scan name (.*)$", (final String scanName) -> {
            if (username == null) {
                username = getInstance().getNessusUsername();
                password = getInstance().getNessusPassword();
            }
            scanClient.login(username, password);
            scanUuid = scanClient.newScan(scanName, policyName, join(hostNames, ","));
            if (nessusVersion == 5) {
                scanIdentifierForStatus = scanName;
            } else {
                scanIdentifierForStatus = scanUuid;
            }
        });

        When("the list of issues is stored", () -> {
            waitForScanToComplete(scanIdentifierForStatus);
            final ReportClient reportClient = createReportClient(nessusUrl, nessusVersion, ignoreHostNamesInSSLCert);
            reportClient.login(username, password);
            issues = reportClient.getAllIssuesSortedByPluginId(scanUuid);
        });
    }

    @Given("the target host names")
    public void setTargetHosts(final List<String> hosts) {
        hostNames.addAll(hosts);
    }

    @Given("a nessus version (\\d+) server at (https?:\\/\\/.+)$")
    public void createNessusClient(final int version, final String url) {
        nessusUrl = url;
        nessusVersion = version;
        scanClient = createScanClient(url, nessusVersion, ignoreHostNamesInSSLCert);
    }

    @When("the following nessus false positive are removed")
    public void removeFalsePositives(final List<NessusFalsePositive> falsePositives) {
        falsePositives.forEach(falsePositive -> {
            final Issue issue = issues.get(falsePositive.getPluginId());
            if (issue != null) {
                issue.getHostnames().remove(falsePositive.getHostname());
                if (issue.getHostnames().size() == 0) {
                    issues.remove(falsePositive.getPluginId());
                }
            }
        });
    }

    @Then("no severity: (\\d+) or higher issues should be present")
    public void verifyRiskOfIssues(final int severity) {
        final List<Issue> notable = issues.values().stream().filter(issue -> issue.getSeverity() >= severity).collect(toList());
        assertThat(notable, empty());
    }

    private void waitForScanToComplete(final String scanName) {
        while (scanClient.isScanRunning(scanName)) {
            try {
                sleep(2000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}