package acceptance.pk.lucidxpo.ynami.cucumber.test;

import acceptance.pk.lucidxpo.ynami.config.cucumber.CucumberContextConfigurationLoader;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

import java.sql.SQLException;

import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_CONTEXT_LOADER;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_HTML_REPORTS_DIR;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_JSON_REPORT;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.CUCUMBER_STEPS_PACKAGE;
import static acceptance.pk.lucidxpo.ynami.cucumber.test.CucumberTest.FEATURE_FILES_LOCATION;
import static io.cucumber.junit.CucumberOptions.SnippetType.CAMELCASE;

@RunWith(Cucumber.class)
@CucumberOptions(
        snippets = CAMELCASE,
        features = {
                FEATURE_FILES_LOCATION
        },
        glue = {
                CUCUMBER_STEPS_PACKAGE,
                CUCUMBER_CONTEXT_LOADER
        },
        plugin = {
                "pretty",
                CUCUMBER_JSON_REPORT,
                CUCUMBER_HTML_REPORTS_DIR,
                "rerun:target/cucumber-api-rerun.txt"
        }
)
public class CucumberTest extends CucumberContextConfigurationLoader {
    static final String FEATURE_FILES_LOCATION = "classpath:cuke/feature/";
    static final String CUCUMBER_HTML_REPORTS_DIR = "html:target/cucumber-reports";
    static final String CUCUMBER_JSON_REPORT = "json:target/cucumber-reports/Cucumber.json";
    static final String CUCUMBER_STEPS_PACKAGE = "acceptance.pk.lucidxpo.ynami.cucumber.steps";
    static final String CUCUMBER_CONTEXT_LOADER = "acceptance.pk.lucidxpo.ynami.config.cucumber";

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
        ((AnnotationConfigServletWebServerApplicationContext) applicationContext).close();
    }
}

@CucumberContextConfiguration
@SpringBootTest(classes = CucumberTest.class)
class CucumberSpringConfiguration {
}
