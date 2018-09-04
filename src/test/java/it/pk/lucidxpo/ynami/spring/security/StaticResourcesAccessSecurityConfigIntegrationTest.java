package it.pk.lucidxpo.ynami.spring.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;
import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.mock.web.MockHttpServletResponse.SC_OK;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class StaticResourcesAccessSecurityConfigIntegrationTest extends AbstractIntegrationTest {
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

        staticResources.addAll(getWebjarsResources());

        for (final String staticResource : staticResources) {
            final MvcResult mvcResult = mockMvc.perform(get(staticResource)).andReturn();
            final int actualStatus = mvcResult.getResponse().getStatus();
            assertEquals(SC_OK, actualStatus, format("Expected %s for %s, but got %s", SC_OK, staticResource, actualStatus));
        }
    }

    private HashSet<String> getWebjarsResources() {
        return newHashSet(
                "/webjars/font-awesome/5.2.0/css/fontawesome.css",
                "/webjars/font-awesome/5.2.0/css/solid.css",
                "/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css",
                "/webjars/jquery/3.3.1-1/jquery.min.js",
                "/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"
        );
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