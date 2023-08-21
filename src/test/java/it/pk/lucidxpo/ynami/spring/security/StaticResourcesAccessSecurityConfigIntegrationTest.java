package it.pk.lucidxpo.ynami.spring.security;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;
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
                /*
                    In the Apple macOS operating system, .DS_Store is a file that stores custom attributes of its
                    containing folder, such as the position of icons or the choice of a background image. The name is
                    an abbreviation of Desktop Services Store, reflecting its purpose. It is created and maintained by
                    the Finder application in every folder, and has functions similar to the file desktop.ini in
                    Microsoft Windows. Starting with a full stop (period) character, it is hidden in Finder and many
                    Unix utilities. Its internal structure is proprietary. Hence, filtering it.
                 */
                .filter(file -> !".DS_Store".equals(file.getName()))
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
                "/webjars/font-awesome/6.4.2/css/fontawesome.css",
                "/webjars/font-awesome/6.4.2/css/solid.css",
                "/webjars/bootstrap/5.3.1/css/bootstrap.min.css",
                "/webjars/jquery/3.7.0/jquery.min.js",
                "/webjars/bootstrap/5.3.1/js/bootstrap.min.js"
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