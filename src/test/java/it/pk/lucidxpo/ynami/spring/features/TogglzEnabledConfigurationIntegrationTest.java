package it.pk.lucidxpo.ynami.spring.features;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.NoOpUserProvider;
import org.togglz.core.user.UserProvider;
import org.togglz.spring.security.SpringSecurityUserProvider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
class TogglzEnabledConfigurationIntegrationTest extends AbstractIntegrationTest {
    @Value("${togglz.console.path:NOT_APPLICABLE}")
    private String togglzConsolePath;

    @Test
    void shouldVerifyThatFeatureProviderBeanExistsWhenTogglzIsEnabled() {
        final FeatureProvider featureProvider = applicationContext.getBean(FeatureProvider.class);
        assertThat(featureProvider.getClass().getSimpleName(), is("CustomFeatureProvider"));
    }

    @Test
    void shouldVerifyThatStateRepositoryBeanExistsWhenConfigPersistableFeatureTogglesIsTrueAndTogglzIsEnabled() throws NoSuchFieldException, IllegalAccessException {
        final StateRepository stateRepository = applicationContext.getBean(StateRepository.class);
        assertThat(getField(stateRepository, "ttl"), is(5000L));

        final JDBCStateRepository jdbcStateRepository = (JDBCStateRepository) getField(stateRepository, "delegate");
        assertThat(jdbcStateRepository, instanceOf(JDBCStateRepository.class));
        assertThat(getField(jdbcStateRepository, "tableName"), is("FeatureToggles"));
    }

    @Test
    void shouldVerifyThatUserProviderBeanExistsWhenTogglzIsEnabled() {
        final UserProvider userProvider = applicationContext.getBean(UserProvider.class);
        if (isSpringSecurityLoaded()) {
            assertThat(userProvider, instanceOf(SpringSecurityUserProvider.class));
        } else {
            assertThat(userProvider, instanceOf(NoOpUserProvider.class));
        }
    }

    @Test
    @WithMockUser
    void shouldVerifyThatTogglzAdminConsoleIsAccessibleWhenTogglzConsoleIsEnabledAndTogglzIsEnabled() throws Exception {
        mockMvc.perform(get(togglzConsolePath))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.spring-boot.actuator.v3+json"))
                .andExpect(content().string(containsString(chooseOneOf(values()).name())));
    }

    private boolean isSpringSecurityLoaded() {
        return null != invokeMethod(getClass().getClassLoader(),
                "findLoadedClass",
                "org.springframework.security.config.annotation.web.configuration.EnableWebSecurity"
        );
    }
}