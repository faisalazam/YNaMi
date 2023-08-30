package penetration.pk.lucidxpo.ynami.steps.defs;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import penetration.pk.lucidxpo.ynami.behaviours.ICors;
import penetration.pk.lucidxpo.ynami.web.Application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CorsSteps {
    private Application app;

    @When("the path (.*) is requested with the HTTP method GET with the 'Origin' header set to (.*)")
    public void thePathIsRequestedWithTheHttpMethodGetWithTheOriginHeaderSetTo(final String path, final String origin) {
        ((ICors) app).makeCorsRequest(path, origin);
    }

    @Then("the returned 'Access-Control-Allow-Origin' header has the value (.*)")
    public void theReturnedAccessControlAllowOriginHeaderHasTheValue(final String origin) {
        final String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
        assertThat("The returned Access-Control-Allow-Origin header equals the Origin", returnedHeader, equalTo(origin));
    }

    @Then("the 'Access-Control-Allow-Origin' header is not returned")
    public void theAccessControlAllowOriginHeaderIsNotReturned() {
        final String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
        assertThat("The header 'Access-Control-Allow-Origin' header was not returned", returnedHeader, equalTo(null));
    }

    public Application getWebApplication() {
        return app;
    }
}