package pk.lucidxpo.ynami.spring.features;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.spi.FeatureProvider;
import pk.lucidxpo.ynami.AbstractIntegrationTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pk.lucidxpo.ynami.spring.features.AvailableFeatures.values;
import static pk.lucidxpo.ynami.testutils.Randomly.chooseOneOf;
import static pk.lucidxpo.ynami.testutils.ReflectionHelper.getField;

@TestPropertySource(properties = {
        "togglz.table.name=FeatureToggles",
        "config.persistable.feature.toggles=true",
        "togglz.caching.state.repository.ttl=5000"
})
public class TogglzConfigurationIntegrationTest extends AbstractIntegrationTest {
    @Value("${togglz.console.path}")
    private String togglzConsolePath;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void shouldVerifyThatFeatureProviderBeanExists() {
        final FeatureProvider featureProvider = applicationContext.getBean(FeatureProvider.class);
        assertThat(featureProvider, instanceOf(EnumBasedFeatureProvider.class));
    }

    @Test
    public void shouldVerifyThatStateRepositoryBeanExistsWhenConfigPersistableFeatureTogglesIsTrue() throws NoSuchFieldException, IllegalAccessException {
        final StateRepository stateRepository = applicationContext.getBean(StateRepository.class);
        assertThat(getField(stateRepository, "ttl"), is(5000L));

        final JDBCStateRepository jdbcStateRepository = (JDBCStateRepository) getField(stateRepository, "delegate");
        assertThat(jdbcStateRepository, instanceOf(JDBCStateRepository.class));
        assertThat(getField(jdbcStateRepository, "tableName"), is("FeatureToggles"));
    }

    @Test
    public void shouldVerifyThatTogglzAdminConsoleIsAccessibleWhenTogglzConsoleIsEnabled() throws Exception {
        mockMvc.perform(get(togglzConsolePath))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.spring-boot.actuator.v2+json;charset=UTF-8"))
                .andExpect(content().string(containsString(chooseOneOf(values()).name())))
                .andReturn();
    }
}