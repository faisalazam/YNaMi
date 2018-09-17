package penetration.pk.lucidxpo.ynami.utils;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.io.FileUtils.deleteDirectory;

public class CucumberReportsGenerator {
    public static void generateReports(final String cucumberReportsPath,
                                       final String cucumberJsonReportPath) throws IOException {

        final String buildNumber = "1";
        final String projectName = "YNaMi";
        final boolean runWithJenkins = false;
        final boolean parallelTesting = false;
        final File reportOutputDirectory = new File(cucumberReportsPath);
        deleteDirectory(reportOutputDirectory);

        final Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        configuration.setBuildNumber(buildNumber);
        configuration.setRunWithJenkins(runWithJenkins);
        configuration.setParallelTesting(parallelTesting);

        final List<String> jsonFiles = newArrayList(cucumberJsonReportPath);
        final ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();

        System.out.println("Report available on: " + reportOutputDirectory.getAbsolutePath() + "/feature-overview.html");
    }
}