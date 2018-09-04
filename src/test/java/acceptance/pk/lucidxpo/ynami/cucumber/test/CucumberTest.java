package acceptance.pk.lucidxpo.ynami.cucumber.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

import static cucumber.api.SnippetType.CAMELCASE;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_HTML_REPORTS_DIR;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_JSON_REPORT;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_STEPS_PACKAGE;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.FEATURE_FILES_LOCATION;

@RunWith(Cucumber.class)
@CucumberOptions(
        snippets = CAMELCASE,
        features = {
                FEATURE_FILES_LOCATION
        },
        glue = {
                CUCUMBER_STEPS_PACKAGE
        },
        plugin = {
                "pretty",
                CUCUMBER_JSON_REPORT,
                CUCUMBER_HTML_REPORTS_DIR
        }
)
public class CucumberTest {
    static final String FEATURE_FILES_LOCATION = "classpath:cuke/feature/";
    static final String CUCUMBER_HTML_REPORTS_DIR = "html:target/cucumber-reports";
    static final String CUCUMBER_JSON_REPORT = "json:target/cucumber-reports/Cucumber.json";
    static final String CUCUMBER_STEPS_PACKAGE = "acceptance.pk.lucidxpo.ynami.cucumber.steps";
}