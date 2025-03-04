package ut.pk.lucidxpo.ynami;

import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;
import pk.lucidxpo.ynami.persistence.model.AuditEntryArchive;
import pk.lucidxpo.ynami.persistence.model.Auditable;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.reflections.ReflectionUtils.getSuperTypes;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith;
import static ut.pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;

class EntityExtendsVerifierTest {

    @Test
    void shouldVerifyThatAllTheEntitiesAreExtendedFromAuditable() {
        final Set<Class<?>> entityClasses = getTypesAnnotatedWith(Entity.class, BASE_PACKAGE + ".persistence.model");

        //The entity classes that don't need to be audited, should not be extended from "Auditable".
        final Set<String> excludedEntityNames = newHashSet(
                AuditEntry.class.getName(),
                AuditEntryArchive.class.getName()
        );

        assertThat("The list of entity classes must not be empty", entityClasses.isEmpty(), is(false));
        assertThat(entityClasses.size() > excludedEntityNames.size(), is(true));

        for (Class<?> entityClazz : entityClasses) {
            if (excludedEntityNames.contains(entityClazz.getName())) {
                assertThat(entityClazz.getName() + " should not be extended from " + Auditable.class.getSimpleName() + ".",
                        getSuperTypes(entityClazz), not(hasItem(Auditable.class))
                );
            } else {
                assertThat(entityClazz.getName() + " should be extended from " + Auditable.class.getSimpleName() + ".",
                        getSuperTypes(entityClazz), hasItem(Auditable.class)
                );
            }
        }
    }
}