package penetration.pk.lucidxpo.ynami.steps.defs;

import cucumber.api.java.en.When;
import cucumber.api.java8.En;
import penetration.pk.lucidxpo.ynami.model.Port;
import penetration.pk.lucidxpo.ynami.scanners.PortScanner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static penetration.pk.lucidxpo.ynami.model.State.fromString;

public class InfrastructureSteps implements En {
    private String targetHost;
    private List<Port> portScanResults;
    private List<Integer> selectedPorts;

    public InfrastructureSteps() {
        Given("the target host name (.*)", (final String hostname) -> targetHost = hostname);

        When("the (.*) ports are selected", (final String state) -> {
            selectedPorts = newArrayList();
            portScanResults.stream()
                    .filter(result -> result.getState().equals(fromString(state)))
                    .forEach(result -> selectedPorts.add(result.getNumber()));
        });

        Then("the ports should be (.*)", (final String csvPorts) -> {
                    final Integer[] expectedPorts = stream(csvPorts.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
                    assertThat("Only the expected ports are open", selectedPorts, containsInAnyOrder(expectedPorts));
                }
        );
    }

    @When("^TCP ports from (\\d+) to (\\d+) are scanned using (\\d+) threads and a timeout of (\\d+) milliseconds$")
    public void scanPorts(final int from, final int to, final int threads, final int timeout) throws ExecutionException, InterruptedException {
        final PortScanner portScanner = new PortScanner(targetHost, from, to, threads, timeout);
        portScanResults = portScanner.scan();
    }
}