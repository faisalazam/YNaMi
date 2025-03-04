package it.pk.lucidxpo.ynami.controller.sample;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestExecutionListeners;
import org.thymeleaf.exceptions.TemplateInputException;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.CONDITIONAL_STATEMENTS_EXECUTION;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.METHOD_EXECUTION;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.TOGGLEABLE_SERVICE;

@WithMockUser
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class SampleFeatureControllerIntegrationTest extends AbstractIntegrationTest {

    // =========================== Verify Feature Toggles are working as expected ===========================

    @Test
    void shouldNotExpectFeatureStatusToBeEnabledWhenCorrespondingFeatureToggleIsEnabled() {
        featureManager.deactivate(METHOD_EXECUTION);
        featureManager.activate(CONDITIONAL_STATEMENTS_EXECUTION);

        try {
            mockMvc.perform(get("/feature-test"));
            fail("TemplateInputException should have been thrown, " +
                    "as empty string for template name is returned " +
                    "because method execution is not allowed due to feature being disabled");
        } catch (Exception e) {
            assertThat(e, instanceOf(ServletException.class));
            assertThat(e.getCause(), instanceOf(TemplateInputException.class));
        }
    }

    @Test
    void shouldExpectFeatureStatusToBeEnabledWhenCorrespondingFeatureToggleIsEnabled() throws Exception {
        featureManager.activate(CONDITIONAL_STATEMENTS_EXECUTION);
        featureManager.activate(METHOD_EXECUTION);
        if (isConfigEnabled("config.togglz.enabled")) {
            mockMvc.perform(get("/feature-test"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("welcome"))
                    .andExpect(model().attribute("message", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled."))
                    .andReturn();
        } else {
            performGetFeatureTest();
        }
    }

    @Test
    void shouldExpectFeatureStatusToBeDisabledWhenCorrespondingFeatureToggleIsNotEnabled() throws Exception {
        featureManager.deactivate(CONDITIONAL_STATEMENTS_EXECUTION);
        featureManager.activate(METHOD_EXECUTION);

        if (isConfigEnabled("config.togglz.enabled")) {
            mockMvc.perform(get("/feature-test"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("welcome"))
                    .andExpect(model().attribute("message", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled."))
                    .andReturn();
        } else {
            performGetFeatureTest();
        }
    }

    @Test
    void shouldInvokeNewToggleableServiceWhenToggleableServiceFeatureToggleIsEnabled() throws Exception {
        featureManager.activate(TOGGLEABLE_SERVICE);

        final String expectedString = isConfigEnabled("config.togglz.enabled")
                ? "Value from new service implementation"
                : "Value from old service implementation";
        mockMvc.perform(get("/some-service"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedString)))
                .andReturn();
    }

    @Test
    void shouldInvokeOldToggleableServiceWhenToggleableServiceFeatureToggleIsNotEnabled() throws Exception {
        featureManager.deactivate(TOGGLEABLE_SERVICE);

        mockMvc.perform(get("/some-service"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Value from old service implementation")))
                .andReturn();
    }

    private void performGetFeatureTest() {
        try {
            mockMvc.perform(get("/feature-test"));
            fail("TemplateInputException should have been thrown, " +
                    "because method execution is not allowed due to Togglz profile being inactive");
        } catch (Exception e) {
            assertThat(e, instanceOf(ServletException.class));
            assertThat(e.getCause(), instanceOf(TemplateInputException.class));
        }
    }
}