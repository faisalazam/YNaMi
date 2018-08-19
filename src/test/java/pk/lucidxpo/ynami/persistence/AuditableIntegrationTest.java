package pk.lucidxpo.ynami.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reflections.Reflections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.model.Auditable;
import pk.lucidxpo.ynami.persistence.model.security.User;
import pk.lucidxpo.ynami.spring.security.UserPrincipal;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;
import pk.lucidxpo.ynami.utils.executionlisteners.TimeFreezeExecutionListener;

import javax.transaction.Transactional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.beans.BeanUtils.instantiateClass;
import static org.springframework.security.test.context.TestSecurityContextHolder.clearContext;
import static org.springframework.security.test.context.TestSecurityContextHolder.getContext;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;
import static org.springframework.util.StringUtils.uncapitalize;
import static pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;
import static pk.lucidxpo.ynami.persistence.model.security.UserBuilder.anUser;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;
import static pk.lucidxpo.ynami.spring.security.UserPrincipal.create;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Transactional
@Sql(statements = "DELETE FROM Roles")
@TestExecutionListeners(mergeMode = MERGE_WITH_DEFAULTS,
        value = {
                DatabaseExecutionListener.class,
                TimeFreezeExecutionListener.class
        }
)
class AuditableIntegrationTest extends AbstractIntegrationTest {

    private static final Set<Class<? extends Auditable>> AUDITABLE_ENTITY_CLASSES = newHashSet();

    @BeforeAll
    static void setup() {
        final Reflections reflections = new Reflections(BASE_PACKAGE + ".persistence.model");
        AUDITABLE_ENTITY_CLASSES.addAll(reflections.getSubTypesOf(Auditable.class));
        assertThat("The list of entity classes must not be empty", AUDITABLE_ENTITY_CLASSES.isEmpty(), is(false));
    }

    @AfterEach
    void close() {
        clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void shouldVerifyThatAllAuditableEntitiesArePersistedWithAuditInfo(final boolean isSecurityEnabled) throws Exception {
        toggleFeature(isSecurityEnabled);

        for (final Class<? extends Auditable> auditableEntityClass : AUDITABLE_ENTITY_CLASSES) {
            final User user = setupAuthentication();
            final String entityName = auditableEntityClass.getName();
            final Object entityBuilder = instantiateClass(forName(entityName + "Builder"));
            final Object entity = invokeMethod(entityBuilder, "build");

            assertNotNull(entity);
            assertAll(
                    () -> assertNull(((Auditable) entity).getCreatedBy()),
                    () -> assertNull(((Auditable) entity).getLastModifiedBy()),
                    () -> assertNull(((Auditable) entity).getCreatedDate()),
                    () -> assertNull(((Auditable) entity).getLastModifiedDate())
            );

            final String entitySimpleName = entity.getClass().getSimpleName();
            final JpaRepository repository = (JpaRepository) applicationContext.getBean(uncapitalize(entitySimpleName) + "Repository");
            final Object savedEntity = repository.saveAndFlush(entity);

            final Auditable auditable = (Auditable) repository.findById(getField(savedEntity, "id")).get();
            assertAuditInfo(auditable, user.getUsername());
        }
    }

    private User setupAuthentication() {
        final User user = anUser().build();
        final UserPrincipal userPrincipal = create(user);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.getPassword(), userPrincipal.getAuthorities());
        getContext().setAuthentication(authentication);
        return user;
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