package acceptance.pk.lucidxpo.ynami.cucumber.test;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectDirectories;
import org.junit.platform.suite.api.Suite;

import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CAMELCASE;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_GLUE_LOCATION;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_REPORTING_PLUGINS;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.FEATURE_FILES_LOCATION;
import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.SNIPPET_TYPE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectDirectories(FEATURE_FILES_LOCATION)
@ConfigurationParameter(key = SNIPPET_TYPE_PROPERTY_NAME, value = CAMELCASE)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = CUCUMBER_GLUE_LOCATION)
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = CUCUMBER_REPORTING_PLUGINS)
// TODO: well designed Cucumber implementation: https://www.swtestacademy.com/selenium-spring-boot-cucumber-junit5-project/
public class CucumberTest {
    static final String CAMELCASE = "camelcase";
    static final String FEATURE_FILES_LOCATION = "src/test/resources/cuke/feature/";

    private static final String CUCUMBER_STEPS_PACKAGE = "acceptance.pk.lucidxpo.ynami.cucumber.steps";
    private static final String CUCUMBER_CONTEXT_LOADER = "acceptance.pk.lucidxpo.ynami.cucumber.config";
    private static final String CUCUMBER_REPORTS_PATH = "target/test-results/cucumber-reports/acceptance";
    private static final String CUCUMBER_JSON_REPORT = "json:" + CUCUMBER_REPORTS_PATH + "/json/cucumber.json";
    private static final String CUCUMBER_HTML_REPORT = "html:" + CUCUMBER_REPORTS_PATH + "/html/cucumber.html";
    private static final String CUCUMBER_RERUN_REPORT = "rerun:" + CUCUMBER_REPORTS_PATH + "/cucumber-api-rerun.txt";

    static final String CUCUMBER_GLUE_LOCATION = CUCUMBER_STEPS_PACKAGE + ", " + CUCUMBER_CONTEXT_LOADER;
    static final String CUCUMBER_REPORTING_PLUGINS = "pretty"
            + ", " + CUCUMBER_JSON_REPORT
            + ", " + CUCUMBER_RERUN_REPORT
            + ", " + CUCUMBER_HTML_REPORT;
//
//    TODO: See how we can enable the following method which got commented after all these upgrades???
//    connection is coming from CucumberContextConfigurationLoader
//    @AfterAll
//    public static void tearDown() throws SQLException {
//        connection.close();
//        ((AnnotationConfigServletWebServerApplicationContext) applicationContext).close();
//    }
}