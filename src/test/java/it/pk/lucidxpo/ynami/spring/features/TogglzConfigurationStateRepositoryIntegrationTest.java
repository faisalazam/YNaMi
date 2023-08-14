package it.pk.lucidxpo.ynami.spring.features;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@TestPropertySource(properties = {
        "config.togglz.enabled=true",
        "config.persistable.feature.toggles=false"
})
class TogglzConfigurationStateRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Test
    void shouldVerifyThatStateRepositoryBeanDoesNotExistWhenConfigPersistableFeatureTogglesIsFalseAndTogglzIsEnabled() {
        final StateRepository stateRepository = applicationContext.getBean(StateRepository.class);
        assertThat(stateRepository, instanceOf(InMemoryStateRepository.class));
    }
}