package ut.pk.lucidxpo.ynami;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.reflections.ReflectionUtils.getSuperTypes;
import static ut.pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith;

class RepositoryExtendsVerifierTest {

    @SuppressWarnings("UnstableApiUsage")
    @Test
    void shouldVerifyThatAllTheRepositoriesAreExtendedFromJpaRepository() {
        final Set<Class<?>> repositoryClasses = getTypesAnnotatedWith(Repository.class, BASE_PACKAGE + ".persistence.dao");

        //The repository classes that don't need to be extended from JpaRepository, should be excluded here.
        final Set<String> excludedRepositoryNames = newHashSet();

        assertThat("The list of repository classes must not be empty", repositoryClasses.isEmpty(), is(false));
        assertThat(repositoryClasses.size() > excludedRepositoryNames.size(), is(true));

        for (Class repositoryClazz : repositoryClasses) {
            if (excludedRepositoryNames.contains(repositoryClazz.getSimpleName())) {
                assertThat(repositoryClazz.getSimpleName() + " should not be extended from " + JpaRepository.class.getSimpleName() + ".",
                        getSuperTypes(repositoryClazz), not(hasItem(JpaRepository.class))
                );
            } else {
                assertThat(repositoryClazz.getSimpleName() + " should be extended from " + JpaRepository.class.getSimpleName() + ".",
                        getSuperTypes(repositoryClazz), hasItem(JpaRepository.class)
                );
            }
        }
    }
}