package pk.lucidxpo.ynami.controller.sample;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.NestedServletException;
import org.thymeleaf.exceptions.TemplateInputException;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrapper;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pk.lucidxpo.ynami.spring.features.AvailableFeatures.CONDITIONAL_STATEMENTS_EXECUTION;
import static pk.lucidxpo.ynami.spring.features.AvailableFeatures.METHOD_EXECUTION;

public class SampleFeatureControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private FeatureManagerWrapper featureManager;

    // =========================== Verify Feature Toggles are working as expected ===========================

    @Test
    public void shouldNotExpectFeatureStatusToBeEnabledWhenCorrespondingFeatureToggleIsEnabled() throws Exception {
        featureManager.deactivate(METHOD_EXECUTION);
        featureManager.activate(CONDITIONAL_STATEMENTS_EXECUTION);

        try {
            mockMvc.perform(get("/feature-test"));
            fail("TemplateInputException should have been thrown, " +
                    "as empty string for template name is returned " +
                    "because method execution is not allowed due to feature being disabled");
        } catch (Exception e) {
            assertThat(e, instanceOf(NestedServletException.class));
            assertThat(e.getCause(), instanceOf(TemplateInputException.class));
        }
    }

    @Test
    public void shouldExpectFeatureStatusToBeEnabledWhenCorrespondingFeatureToggleIsEnabled() throws Exception {
        featureManager.activate(CONDITIONAL_STATEMENTS_EXECUTION);
        featureManager.activate(METHOD_EXECUTION);

        mockMvc.perform(get("/feature-test"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(model().attribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled."))
                .andReturn();
    }

    @Test
    public void shouldExpectFeatureStatusToBeDisabledWhenCorrespondingFeatureToggleIsNotEnabled() throws Exception {
        featureManager.deactivate(CONDITIONAL_STATEMENTS_EXECUTION);
        featureManager.activate(METHOD_EXECUTION);

        mockMvc.perform(get("/feature-test"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(model().attribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled."))
                .andReturn();
    }
}