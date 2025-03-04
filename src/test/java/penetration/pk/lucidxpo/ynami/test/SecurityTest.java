package penetration.pk.lucidxpo.ynami.test;

import io.cucumber.java.AfterAll;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import java.io.IOException;

import static io.cucumber.core.options.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.SNIPPET_TYPE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CAMELCASE;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CUCUMBER_GLUE_LOCATION;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.CUCUMBER_REPORTING_PLUGINS;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.FEATURE_FILES_LOCATION;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.NAMING_STRATEGY;
import static penetration.pk.lucidxpo.ynami.test.SecurityTest.TAGS;
import static penetration.pk.lucidxpo.ynami.utils.CucumberReportsGenerator.generateReports;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource(FEATURE_FILES_LOCATION)
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = TAGS)
@ConfigurationParameter(key = SNIPPET_TYPE_PROPERTY_NAME, value = CAMELCASE)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = CUCUMBER_GLUE_LOCATION)
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = CUCUMBER_REPORTING_PLUGINS)
@ConfigurationParameter(key = JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME, value = NAMING_STRATEGY)
public class SecurityTest {
    // Camelcase SNIPPET_TYPE will tell Cucumber to generate the method names for Step Definitions in CAMELCASE
    static final String CAMELCASE = "camelcase";
    static final String NAMING_STRATEGY = "long";
    static final String TAGS = "not @wip and not @slow and not @broken-since-zap-2.13.0";
    static final String FEATURE_FILES_LOCATION = "cuke/security/features/";

    private static final String CUCUMBER_REPORTS_PATH = "target/site/cucumber-reports/security";
    public static final String CUCUMBER_HTML_REPORTS_PATH = CUCUMBER_REPORTS_PATH + "/html";
    public static final String CUCUMBER_JSON_REPORT_PATH = CUCUMBER_REPORTS_PATH + "/json/cucumber.json";

    private static final String PEN_TESTS_JSON_REPORT = "json:" + CUCUMBER_JSON_REPORT_PATH;
    private static final String PEN_TESTS_JUNIT_REPORT = "junit:" + CUCUMBER_REPORTS_PATH + "/pen_tests.xml";
    private static final String CUCUMBER_HTML_REPORT = "html:" + CUCUMBER_HTML_REPORTS_PATH + "/cucumber.html";
    private static final String CUCUMBER_RERUN_REPORT = "rerun:" + CUCUMBER_REPORTS_PATH + "/cucumber-api-rerun.txt";

    private static final String CUCUMBER_HOOKS_PACKAGE = "penetration.pk.lucidxpo.ynami.hooks";
    private static final String CUCUMBER_STEP_DEFS_PACKAGE = "penetration.pk.lucidxpo.ynami.steps.defs";
    private static final String CUCUMBER_CONTEXT_LOADER = "penetration.pk.lucidxpo.ynami.config.cucumber";
    private static final String CUCUMBER_STEPS_CONFIG_PACKAGE = "penetration.pk.lucidxpo.ynami.steps.config";

    static final String CUCUMBER_GLUE_LOCATION = CUCUMBER_STEP_DEFS_PACKAGE
            + ", " + CUCUMBER_STEPS_CONFIG_PACKAGE
            + ", " + CUCUMBER_HOOKS_PACKAGE
            + ", " + CUCUMBER_CONTEXT_LOADER;

    static final String CUCUMBER_REPORTING_PLUGINS = "pretty"
            + ", " + PEN_TESTS_JSON_REPORT
            + ", " + CUCUMBER_RERUN_REPORT
            + ", " + PEN_TESTS_JUNIT_REPORT
            + ", " + CUCUMBER_HTML_REPORT;

    @AfterAll
    public static void tearDown() throws IOException {
        // TODO: do I need to generate reports explicitly? I think the ones generated through maven will be good enough.
        //  Or consider combining the reports from cluecumber-report-plugin and maven-cucumber-reporting plugins.
        generateReports(CUCUMBER_HTML_REPORTS_PATH, CUCUMBER_JSON_REPORT_PATH);
    }
}