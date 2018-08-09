package pk.lucidxpo.ynami.spring.features;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.UserProvider;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import java.lang.reflect.InvocationTargetException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "config.togglz.enabled=false"
})
public class TogglzDisabledConfigurationIntegrationTest extends AbstractIntegrationTest {
    @Value("${togglz.console.path:NOT_APPLICABLE}")
    private String togglzConsolePath;

    @Test
    public void shouldVerifyThatFeatureProviderBeanDoesNotExistWhenTogglzIsDisabled() {
        assertBeanDoesNotExist(FeatureProvider.class);
    }

    @Test
    public void shouldVerifyThatStateRepositoryBeanDoesNotExistWhenTogglzIsDisabled() throws NoSuchFieldException, IllegalAccessException {
        assertBeanDoesNotExist(StateRepository.class);
    }

    @Test
    public void shouldVerifyThatUserProviderBeanDoesNotExistWhenTogglzIsDisabled() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertBeanDoesNotExist(UserProvider.class);
    }

    @Test
    @WithMockUser
    public void shouldVerifyThatTogglzAdminConsoleIsNotAccessibleWhenTogglzConsoleIsEnabledAndTogglzIsDisabled() throws Exception {
        mockMvc.perform(get(togglzConsolePath)).andExpect(status().isNotFound());
    }
}