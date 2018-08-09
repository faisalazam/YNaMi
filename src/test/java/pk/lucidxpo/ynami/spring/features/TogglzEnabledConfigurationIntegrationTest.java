package pk.lucidxpo.ynami.spring.features;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.NoOpUserProvider;
import org.togglz.core.user.UserProvider;
import org.togglz.spring.security.SpringSecurityUserProvider;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.values;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getField;

@TestPropertySource(properties = {
        "config.togglz.enabled=true",
        "togglz.table.name=FeatureToggles",
        "config.persistable.feature.toggles=true",
        "togglz.caching.state.repository.ttl=5000"
})
public class TogglzEnabledConfigurationIntegrationTest extends AbstractIntegrationTest {
    @Value("${togglz.console.path:NOT_APPLICABLE}")
    private String togglzConsolePath;

    @Test
    public void shouldVerifyThatFeatureProviderBeanExistsWhenTogglzIsEnabled() {
        final FeatureProvider featureProvider = applicationContext.getBean(FeatureProvider.class);
        assertThat(featureProvider, instanceOf(CustomFeatureProvider.class));
    }

    @Test
    public void shouldVerifyThatStateRepositoryBeanExistsWhenConfigPersistableFeatureTogglesIsTrueAndTogglzIsEnabled() throws NoSuchFieldException, IllegalAccessException {
        final StateRepository stateRepository = applicationContext.getBean(StateRepository.class);
        assertThat(getField(stateRepository, "ttl"), is(5000L));

        final JDBCStateRepository jdbcStateRepository = (JDBCStateRepository) getField(stateRepository, "delegate");
        assertThat(jdbcStateRepository, instanceOf(JDBCStateRepository.class));
        assertThat(getField(jdbcStateRepository, "tableName"), is("FeatureToggles"));
    }

    @Test
    public void shouldVerifyThatUserProviderBeanExistsWhenTogglzIsEnabled() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final UserProvider userProvider = applicationContext.getBean(UserProvider.class);
        if (isSpringSecurityLoaded()) {
            assertThat(userProvider, instanceOf(SpringSecurityUserProvider.class));
        } else {
            assertThat(userProvider, instanceOf(NoOpUserProvider.class));
        }
    }

    @Test
    public void shouldVerifyThatTogglzAdminConsoleIsAccessibleWhenTogglzConsoleIsEnabledAndTogglzIsEnabled() throws Exception {
        mockMvc.perform(get(togglzConsolePath))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.spring-boot.actuator.v2+json;charset=UTF-8"))
                .andExpect(content().string(containsString(chooseOneOf(values()).name())));
    }

    private boolean isSpringSecurityLoaded() {
        return null != invokeMethod(getClass().getClassLoader(), "findLoadedClass", "org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
    }
}