package pk.lucidxpo.ynami;

import com.google.common.reflect.ClassPath;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.reflect.ClassPath.from;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;

class RepositoryExtendsVerifierTest {

    @SuppressWarnings("UnstableApiUsage")
    @Test
    void shouldVerifyThatAllTheRepositoriesAreExtendedFromJpaRepository() throws Exception {

        final ClassLoader loader = currentThread().getContextClassLoader();
        final List<Class> repositoryClasses = from(loader).getTopLevelClassesRecursive(BASE_PACKAGE + ".persistence.dao")
                .stream()
                .map(ClassPath.ClassInfo::load)
                .filter(entityClass -> entityClass.isAnnotationPresent(Repository.class))
                .collect(toList());

        //The repository classes that don't need to be extended from JpaRepository, should be excluded here.
        final Set<String> excludedRepositoryNames = newHashSet();

        assertThat("The list of repository classes must not be empty", repositoryClasses.isEmpty(), is(false));
        assertThat(repositoryClasses.size() > excludedRepositoryNames.size(), is(true));

        for (Class repositoryClazz : repositoryClasses) {
            if (excludedRepositoryNames.contains(repositoryClazz.getSimpleName())) {
                assertThat(repositoryClazz.getSimpleName() + " should not be extended from " + JpaRepository.class.getSimpleName() + ".",
                        repositoryClazz.getInterfaces()[0].getName(), not(JpaRepository.class.getName())
                );
            } else {
                assertThat(repositoryClazz.getSimpleName() + " should be extended from " + JpaRepository.class.getSimpleName() + ".",
                        repositoryClazz.getInterfaces()[0].getName(), is(JpaRepository.class.getName())
                );
            }
        }
    }
}