package penetration.pk.lucidxpo.ynami.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import java.io.IOException;

import static cucumber.api.SnippetType.CAMELCASE;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CUCUMBER_JSON_REPORT_PATH;
import static penetration.pk.lucidxpo.ynami.utils.CucumberReportsGenerator.generateReports;
import static penetration.pk.lucidxpo.ynami.web.drivers.DriverFactory.quitAll;
import static penetration.pk.lucidxpo.ynami.zaputils.boot.Zap.stopZap;

@RunWith(Cucumber.class)
@CucumberOptions(
        snippets = CAMELCASE,
        tags = {
                "not @wip",
                "not @slow"
        },
        features = {
                "classpath:cuke/security/features/"
        },
        plugin = {
                "pretty",
                "json:" + CUCUMBER_JSON_REPORT_PATH,
                "junit:target/reports/security/all_tests.xml"
        },
        glue = {
                "penetration.pk.lucidxpo.ynami.steps.defs",
                "penetration.pk.lucidxpo.ynami.steps.config"
        }
)
public class SecurityTest {
    private static final String CUCUMBER_REPORTS_PATH = "target/reports/security/cucumber";
    private static final String CUCUMBER_HTML_REPORTS_PATH = CUCUMBER_REPORTS_PATH + "/html";
    static final String CUCUMBER_JSON_REPORT_PATH = CUCUMBER_REPORTS_PATH + "/json-report.json";

    @AfterClass
    public static void tearDown() throws IOException {
        generateReports(CUCUMBER_HTML_REPORTS_PATH, CUCUMBER_JSON_REPORT_PATH);
        quitAll();
        stopZap();
    }
}