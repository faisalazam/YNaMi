package penetration.pk.lucidxpo.ynami.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import penetration.pk.lucidxpo.ynami.config.cucumber.CucumberContextConfigurationLoader;

import java.io.IOException;

import static cucumber.api.SnippetType.CAMELCASE;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CUCUMBER_CONTEXT_LOADER;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CUCUMBER_STEPS_CONFIG_PACKAGE;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CUCUMBER_STEP_DEFS_PACKAGE;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.FEATURE_FILES_LOCATION;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.PEN_TESTS_JSON_REPORT;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.PEN_TESTS_JUNIT_REPORT;
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
                FEATURE_FILES_LOCATION
        },
        plugin = {
                "pretty",
                PEN_TESTS_JSON_REPORT,
                PEN_TESTS_JUNIT_REPORT
        },
        glue = {
                CUCUMBER_CONTEXT_LOADER,
                CUCUMBER_STEP_DEFS_PACKAGE,
                CUCUMBER_STEPS_CONFIG_PACKAGE
        }
)
public class SecurityTest extends CucumberContextConfigurationLoader {
    private static final String CUCUMBER_REPORTS_PATH = "target/reports/security/cucumber";
    private static final String CUCUMBER_HTML_REPORTS_PATH = CUCUMBER_REPORTS_PATH + "/html";
    private static final String CUCUMBER_JSON_REPORT_PATH = CUCUMBER_REPORTS_PATH + "/json-report.json";

    static final String PEN_TESTS_JSON_REPORT = "json:" + CUCUMBER_JSON_REPORT_PATH;
    static final String FEATURE_FILES_LOCATION = "classpath:cuke/security/features/";
    static final String PEN_TESTS_JUNIT_REPORT = "junit:target/reports/security/pen_tests.xml";
    static final String CUCUMBER_STEP_DEFS_PACKAGE = "penetration.pk.lucidxpo.ynami.steps.defs";
    static final String CUCUMBER_CONTEXT_LOADER = "penetration.pk.lucidxpo.ynami.config.cucumber";
    static final String CUCUMBER_STEPS_CONFIG_PACKAGE = "penetration.pk.lucidxpo.ynami.steps.config";

    @AfterClass
    public static void tearDown() throws IOException {
        generateReports(CUCUMBER_HTML_REPORTS_PATH, CUCUMBER_JSON_REPORT_PATH);
        quitAll();
        stopZap();

        ((AnnotationConfigServletWebServerApplicationContext) applicationContext).close();
    }
}