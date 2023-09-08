package bdd.pk.lucidxpo.ynami.test;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static bdd.pk.lucidxpo.ynami.test.CucumberTest.CAMELCASE;
import static bdd.pk.lucidxpo.ynami.test.CucumberTest.CUCUMBER_GLUE_LOCATION;
import static bdd.pk.lucidxpo.ynami.test.CucumberTest.CUCUMBER_REPORTING_PLUGINS;
import static bdd.pk.lucidxpo.ynami.test.CucumberTest.FEATURE_FILES_LOCATION;
import static bdd.pk.lucidxpo.ynami.test.CucumberTest.NAMING_STRATEGY;
import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.SNIPPET_TYPE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource(FEATURE_FILES_LOCATION)
@ConfigurationParameter(key = SNIPPET_TYPE_PROPERTY_NAME, value = CAMELCASE)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = CUCUMBER_GLUE_LOCATION)
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = CUCUMBER_REPORTING_PLUGINS)
@ConfigurationParameter(key = JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME, value = NAMING_STRATEGY)
// TODO: well designed Cucumber implementation: https://www.swtestacademy.com/selenium-spring-boot-cucumber-junit5-project/
public class CucumberTest {
    // Camelcase SNIPPET_TYPE will tell Cucumber to generate the method names for Step Definitions in CAMELCASE
    static final String CAMELCASE = "camelcase";
    // Cucumber tests results are reported in a <Class Name> - <Method Name> format. As a result only scenario names or
    // example numbers are reported. This can make for hard to read reports. By using
    // cucumber.junit-platform.naming-strategy=long, Cucumber will include the feature name in the scenario name. This
    // makes the test results legible.
    static final String NAMING_STRATEGY = "long";
    static final String FEATURE_FILES_LOCATION = "cuke/feature/";

    private static final String CUCUMBER_STEPS_PACKAGE = "bdd.pk.lucidxpo.ynami.steps";
    private static final String CUCUMBER_CONTEXT_LOADER = "bdd.pk.lucidxpo.ynami.config";
    private static final String CUCUMBER_WEB_DRIVER_CONFIG = "bdd.pk.lucidxpo.ynami.webdriver";
    private static final String CUCUMBER_REPORTS_PATH = "target/site/cucumber-reports/bdd";
    private static final String CUCUMBER_JSON_REPORT = "json:" + CUCUMBER_REPORTS_PATH + "/json/cucumber.json";
    private static final String CUCUMBER_HTML_REPORT = "html:" + CUCUMBER_REPORTS_PATH + "/html/cucumber.html";
    private static final String CUCUMBER_RERUN_REPORT = "rerun:" + CUCUMBER_REPORTS_PATH + "/cucumber-api-rerun.txt";

    static final String CUCUMBER_GLUE_LOCATION = CUCUMBER_STEPS_PACKAGE
            + ", " + CUCUMBER_CONTEXT_LOADER
            + ", " + CUCUMBER_WEB_DRIVER_CONFIG;
    static final String CUCUMBER_REPORTING_PLUGINS = "pretty"
            + ", " + CUCUMBER_JSON_REPORT
            + ", " + CUCUMBER_RERUN_REPORT
            + ", " + CUCUMBER_HTML_REPORT;
}