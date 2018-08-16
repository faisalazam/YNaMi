package pk.lucidxpo.ynami.spring.features;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.UserProvider;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "config.togglz.enabled=false"
})
class TogglzDisabledConfigurationIntegrationTest extends AbstractIntegrationTest {
    @Value("${togglz.console.path:NOT_APPLICABLE}")
    private String togglzConsolePath;

    @Test
    void shouldVerifyThatFeatureProviderBeanDoesNotExistWhenTogglzIsDisabled() {
        assertBeanDoesNotExist(FeatureProvider.class);
    }

    @Test
    void shouldVerifyThatStateRepositoryBeanDoesNotExistWhenTogglzIsDisabled() {
        assertBeanDoesNotExist(StateRepository.class);
    }

    @Test
    void shouldVerifyThatUserProviderBeanDoesNotExistWhenTogglzIsDisabled() {
        assertBeanDoesNotExist(UserProvider.class);
    }

    @Test
    @WithMockUser
    void shouldVerifyThatTogglzAdminConsoleIsNotAccessibleWhenTogglzConsoleIsEnabledAndTogglzIsDisabled() throws Exception {
        mockMvc.perform(get(togglzConsolePath)).andExpect(status().isNotFound());
    }
}