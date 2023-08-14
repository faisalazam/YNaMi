package ut.pk.lucidxpo.ynami.controller.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import pk.lucidxpo.ynami.controller.sample.SampleFeatureController;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.CONDITIONAL_STATEMENTS_EXECUTION;

@ExtendWith(MockitoExtension.class)
class SampleFeatureControllerTest {
    private MockMvc mockMvc;

    @Mock
    private FeatureManagerWrappable featureManager;

    @InjectMocks
    private SampleFeatureController sampleFeatureController;

    @BeforeEach
    void setup() {
        mockMvc = standaloneSetup(sampleFeatureController).build();
    }
    // =========================== Verify Feature Toggles are working as expected ===========================

    @Test
    void shouldExpectEnabledMessageWhenCorrespondingFeatureToggleIsEnabled() throws Exception {
        given(featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)).willReturn(true);

        mockMvc.perform(get("/feature-test"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("message", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled."))
                .andReturn();
    }

    @Test
    void shouldExpectDisabledMessageWhenCorrespondingFeatureToggleIsNotEnabled() throws Exception {
        given(featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)).willReturn(false);

        mockMvc.perform(get("/feature-test"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("message", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled."))
                .andReturn();
    }
}