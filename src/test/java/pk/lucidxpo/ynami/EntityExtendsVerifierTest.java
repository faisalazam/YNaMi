package pk.lucidxpo.ynami;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;
import pk.lucidxpo.ynami.persistence.model.AuditEntryArchive;
import pk.lucidxpo.ynami.persistence.model.Auditable;

import javax.persistence.Entity;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;

class EntityExtendsVerifierTest {

    @Test
    void shouldVerifyThatAllTheEntitiesAreExtendedFromAuditable() throws Exception {

        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        final Set<BeanDefinition> entityClasses = provider.findCandidateComponents(BASE_PACKAGE + ".persistence.model");

        //The entity classes that don't need to be audited, should not be extended from "Auditable".
        final Set<String> excludedEntityNames = newHashSet(
                AuditEntry.class.getName(),
                AuditEntryArchive.class.getName()
        );

        assertThat("The list of entity classes must not be empty", entityClasses.isEmpty(), is(false));
        assertThat(entityClasses.size() > excludedEntityNames.size(), is(true));

        for (BeanDefinition bean : entityClasses) {
            final Class<?> entityClazz = forName(bean.getBeanClassName());
            if (excludedEntityNames.contains(bean.getBeanClassName())) {
                assertThat(bean.getBeanClassName() + " should not be extended from " + Auditable.class.getSimpleName() + ".",
                        entityClazz.getSuperclass().getName(), not(Auditable.class.getName())
                );
            } else {
                assertThat(bean.getBeanClassName() + " should be extended from " + Auditable.class.getSimpleName() + ".",
                        entityClazz.getSuperclass().getName(), is(Auditable.class.getName())
                );
            }
        }
    }
}