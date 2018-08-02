package pk.lucidxpo.ynami.controller.sample;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrapper;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static pk.lucidxpo.ynami.spring.features.AvailableFeatures.CONDITIONAL_STATEMENTS_EXECUTION;

@RunWith(MockitoJUnitRunner.class)
public class SampleFeatureControllerTest {
    private MockMvc mockMvc;

    @Mock
    private FeatureManagerWrapper featureManager;

    @InjectMocks
    private SampleFeatureController sampleFeatureController;

    @Before
    public void setup() {
        mockMvc = standaloneSetup(sampleFeatureController).build();
    }
    // =========================== Verify Feature Toggles are working as expected ===========================

    @Test
    public void shouldExpectFeatureStatusToBeEnabledWhenCorrespondingFeatureToggleIsEnabled() throws Exception {
        given(featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)).willReturn(true);

        mockMvc.perform(get("/feature-test"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled."))
                .andReturn();
    }

    @Test
    public void shouldExpectFeatureStatusToBeDisabledWhenCorrespondingFeatureToggleIsNotEnabled() throws Exception {
        given(featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)).willReturn(false);

        mockMvc.perform(get("/feature-test"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled."))
                .andReturn();
    }
}