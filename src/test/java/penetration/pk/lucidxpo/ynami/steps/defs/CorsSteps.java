package penetration.pk.lucidxpo.ynami.steps.defs;

import io.cucumber.java8.En;
import penetration.pk.lucidxpo.ynami.behaviours.ICors;
import penetration.pk.lucidxpo.ynami.web.Application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CorsSteps implements En {
    private Application app;

    public CorsSteps() {
        When("the path (.*) is requested with the HTTP method GET with the 'Origin' header set to (.*)",
                (final String path, final String origin) -> ((ICors) app).makeCorsRequest(path, origin));

        Then("the returned 'Access-Control-Allow-Origin' header has the value (.*)", (final String origin) -> {
            final String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
            assertThat("The returned Access-Control-Allow-Origin header equals the Origin", returnedHeader, equalTo(origin));
        });

        Then("the 'Access-Control-Allow-Origin' header is not returned", () -> {
            final String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
            assertThat("The header 'Access-Control-Allow-Origin' header was not returned", returnedHeader, equalTo(null));
        });
    }

    public Application getWebApplication() {
        return app;
    }
}