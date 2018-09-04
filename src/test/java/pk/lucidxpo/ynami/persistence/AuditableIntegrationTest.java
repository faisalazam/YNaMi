package pk.lucidxpo.ynami.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.reflections.Reflections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.model.Auditable;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;
import pk.lucidxpo.ynami.utils.executionlisteners.TimeFreezeExecutionListener;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.beans.BeanUtils.instantiateClass;
import static org.springframework.security.test.context.TestSecurityContextHolder.clearContext;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;
import static org.springframework.util.StringUtils.uncapitalize;
import static ut.pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;
import static pk.lucidxpo.ynami.spring.security.helper.AuthenticationSetter.setupAuthentication;

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

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllAuditableEntitiesArePersistedWithAuditInfoWhenWebSecurityIsEnabled() {
        featureManager.activate(WEB_SECURITY);
        assertTrue(featureManager.isActive(WEB_SECURITY));

        return AUDITABLE_ENTITY_CLASSES.stream()
                .map(auditableEntityClass -> {
                    final String displayName = auditableEntityClass.getSimpleName() + " entity is persisted with audit info";
                    return dynamicTest(displayName, () -> {
                        final UserDetails user = setupAuthentication();
                        saveAndAssert(auditableEntityClass, user.getUsername());
                    });
                })
                .collect(toList());
    }

    @TestFactory
    Collection<DynamicTest> shouldVerifyThatAllAuditableEntitiesArePersistedWithAuditInfoWhenWebSecurityIsDisabled() {
        featureManager.deactivate(WEB_SECURITY);
        assertFalse(featureManager.isActive(WEB_SECURITY));

        return AUDITABLE_ENTITY_CLASSES.stream()
                .map(auditableEntityClass -> {
                    final String displayName = auditableEntityClass.getSimpleName() + " entity is persisted with audit info";
                    return dynamicTest(displayName, () -> saveAndAssert(auditableEntityClass, ""));
                })
                .collect(toList());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void saveAndAssert(final Class<? extends Auditable> auditableEntityClass, final String username) throws ClassNotFoundException {
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
        assertAuditInfo(auditable, username);
    }
}