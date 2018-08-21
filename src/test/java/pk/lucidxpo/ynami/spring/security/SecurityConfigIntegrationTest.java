package pk.lucidxpo.ynami.spring.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestExecutionListeners;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class SecurityConfigIntegrationTest extends AbstractIntegrationTest {
    private static final String STATIC_RESOURCES_PATH = "src/main/resources/static";

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void shouldVerifyThatAllStaticResourcesAreAccessibleRegardlessOfWebSecurity(final boolean isSecurityEnabled) throws Exception {
        toggleFeature(isSecurityEnabled);

        final File directory = new File(STATIC_RESOURCES_PATH);
        final List<String> staticResources = listFiles(directory, null, true)
                .stream()
                .map(file -> file.getPath().replace(STATIC_RESOURCES_PATH, EMPTY))
                .collect(toList());
        assertFalse(staticResources.isEmpty());
        for (String staticResource : staticResources) {
            mockMvc.perform(get(staticResource)).andExpect(status().isOk());
        }
    }

    private void toggleFeature(final boolean enableDisable) {
        if (enableDisable) {
            featureManager.activate(WEB_SECURITY);
            assertTrue(featureManager.isActive(WEB_SECURITY));
        } else {
            featureManager.deactivate(WEB_SECURITY);
            assertFalse(featureManager.isActive(WEB_SECURITY));
        }
    }
}