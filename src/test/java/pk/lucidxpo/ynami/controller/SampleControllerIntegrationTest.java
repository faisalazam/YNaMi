package pk.lucidxpo.ynami.controller;

import org.junit.Test;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SampleControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/",
                String.class)).contains("Welcome Faisal");
    }
}