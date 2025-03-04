package penetration.pk.lucidxpo.ynami.steps.defs;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.continuumsecurity.jsslyze.JSSLyze;
import penetration.pk.lucidxpo.ynami.config.Config;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static penetration.pk.lucidxpo.ynami.featureworld.ScenariosAwareWorld.getInstance;

public class SSLyzeSteps {
    private final static String OUTFILENAME = "sslyze.output";

    @When("the SSLyze command is run against the application")
    public void theSslyzeCommandIsRunAgainstTheApplication() throws Exception {
        if (!getInstance().isSslRunCompleted()) {
            final Config configInstance = Config.getInstance();
            final int port = configInstance.getSslPort();
            final String host = configInstance.getSslHost();
            final JSSLyze jSSLLyze = new JSSLyze(configInstance.getSSLyzePath(), OUTFILENAME);
            jSSLLyze.execute(configInstance.getSSLyzeOption(), host, port);
            getInstance().setJSSLyze(jSSLLyze);
            getInstance().setSslRunCompleted(true);
        }
    }

    @Then("the output must contain the text (.*)")
    public void theOutputMustContainTheText(String text) {
        if (text.startsWith("\"") || text.startsWith("'")) {
            text = text.substring(1, text.length() - 1);
        }
        assertThat(getJssLyze().getOutput(), containsString(text));
    }

    @Then("^the output must contain a line that matches (.*)")
    public void theOutputMustContainALineThatMatches(String regex) {
        if (regex.startsWith("\"") || regex.startsWith("'")) {
            regex = regex.substring(1, regex.length() - 1);
        }
        assertThat(getJssLyze().getParser().doesAnyLineMatch(regex), equalTo(true));
    }

    @Then("the minimum key size must be {int} bits")
    public void verifyMinimumKeySize(final int size) {
        assertThat(getJssLyze().getParser().findSmallestAcceptedKeySize(), greaterThanOrEqualTo(size));
    }

    @Then("the following protocols must not be supported")
    public void verifyDisabledProtocols(final List<String> forbiddenProtocols) {
        final List<String> supported = getJssLyze().getParser().listAllSupportedProtocols();
        forbiddenProtocols.forEach(forbidden -> assertThat(supported, not(hasItem(forbidden))));
    }

    @Then("the following protocols must be supported")
    public void verifySupportedProtocols(final List<String> mandatoryProtocols) {
        final List<String> supported = getJssLyze().getParser().listAllSupportedProtocols();
        mandatoryProtocols.forEach(mandatory -> assertThat(supported, hasItem(mandatory)));

    }

    @Then("any of the following ciphers must be supported")
    public void verifyAnyCipherSupported(final List<String> ciphers) {
        boolean foundCipher = false;
        for (final String cipher : ciphers) {
            if (getJssLyze().getParser().acceptsCipherWithPartialName(cipher)) {
                foundCipher = true;
            }
        }
        assertThat(foundCipher, equalTo(true));
    }

    private JSSLyze getJssLyze() {
        return getInstance().getJSSLyze();
    }
}