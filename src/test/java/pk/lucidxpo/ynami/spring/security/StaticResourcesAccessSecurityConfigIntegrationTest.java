package pk.lucidxpo.ynami.spring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

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
import static org.springframework.mock.web.MockHttpServletResponse.SC_OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

class StaticResourcesAccessSecurityConfigIntegrationTest extends AbstractIntegrationTest {
    private static final String STATIC_RESOURCES_PATH = "src/main/resources/static";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void before() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldVerifyThatAllStaticResourcesAreAccessibleRegardlessOfWebSecurity() throws Exception {
        final File directory = new File(STATIC_RESOURCES_PATH);
        final List<String> staticResources = listFiles(directory, null, true)
                .stream()
                .map(file -> file.getPath().replace(STATIC_RESOURCES_PATH, EMPTY))
                .collect(toList());
        assertFalse(staticResources.isEmpty());

        staticResources.addAll(getWebjarsResources());

        for (String staticResource : staticResources) {
            final MvcResult mvcResult = mockMvc.perform(get(staticResource)).andReturn();//.andExpect(status().isOk());
            final int actualStatus = mvcResult.getResponse().getStatus();
            assertEquals(SC_OK, actualStatus, format("Expected %s for %s, but got %s", SC_OK, staticResource, actualStatus));
        }
    }

    private HashSet<String> getWebjarsResources() {
        return newHashSet(
                "/webjars/font-awesome/5.1.0/css/fontawesome.css",
                "/webjars/font-awesome/5.1.0/css/solid.css",
                "/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css",
                "/webjars/jquery/3.3.1-1/jquery.min.js",
                "/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"
        );
    }
}