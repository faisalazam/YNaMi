package pk.lucidxpo.ynami;

import com.google.common.reflect.ClassPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.reflect.ClassPath.from;
import static java.lang.Thread.currentThread;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PackageVerifierTest {
    public static final String BASE_PACKAGE = "pk.lucidxpo.ynami";

    @Test
    void shouldVerifyThatAllTheControllersAreDefinedInsideControllerPackage() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        final Set<BeanDefinition> allControllerClasses = provider.findCandidateComponents(BASE_PACKAGE);
        final Set<BeanDefinition> controllerClassesInControllerPackage = provider.findCandidateComponents(BASE_PACKAGE + ".controller");

        assertThat(controllerClassesInControllerPackage.isEmpty(), is(false));
        assertThat(controllerClassesInControllerPackage.size(), is(allControllerClasses.size()));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Test
    void shouldVerifyThatAllTheRepositoriesAreDefinedInsideDaoPackage() throws IOException {
        final ClassLoader loader = currentThread().getContextClassLoader();
        final List<Class> allRepositoryClasses = from(loader).getTopLevelClassesRecursive(BASE_PACKAGE)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .filter(entityClass -> entityClass.isAnnotationPresent(Repository.class))
                .collect(toList());
        final List<Class> repositoryClassesInDaoPackage = from(loader).getTopLevelClassesRecursive(BASE_PACKAGE + ".persistence.dao")
                .stream()
                .map(ClassPath.ClassInfo::load)
                .filter(entityClass -> entityClass.isAnnotationPresent(Repository.class))
                .collect(toList());

        assertThat(repositoryClassesInDaoPackage.isEmpty(), is(false));
        assertThat(repositoryClassesInDaoPackage.size(), is(allRepositoryClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheDtosAreDefinedInsideDtoPackage() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(compile(".*DTO$")));
        final Set<BeanDefinition> allDtoClasses = provider.findCandidateComponents(BASE_PACKAGE);
        final Set<BeanDefinition> dtoClassesInDtoPackage = provider.findCandidateComponents(BASE_PACKAGE + ".persistence.dto");

        assertThat(dtoClassesInDtoPackage.isEmpty(), is(false));
        assertThat(dtoClassesInDtoPackage.size(), is(allDtoClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheEntityBuildersAreDefinedInsideBuilderPackage() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(compile(".*Builder$")));
        provider.addExcludeFilter(new RegexPatternTypeFilter(compile(".*DTOBuilder$")));
        final Set<BeanDefinition> allBuilderClasses = provider.findCandidateComponents(BASE_PACKAGE);
        final Set<BeanDefinition> builderClassesInBuilderPackage = provider.findCandidateComponents(BASE_PACKAGE + ".persistence.model");

        assertThat(builderClassesInBuilderPackage.isEmpty(), is(false));
        assertThat(builderClassesInBuilderPackage.size(), is(allBuilderClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheServicesAreDefinedInsideServicePackage() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        final Set<BeanDefinition> allServiceClasses = provider.findCandidateComponents(BASE_PACKAGE);
        final Set<BeanDefinition> serviceClassesInServicePackage = provider.findCandidateComponents(BASE_PACKAGE + ".service");

        assertThat(serviceClassesInServicePackage.isEmpty(), is(false));
        assertThat(serviceClassesInServicePackage.size(), is(allServiceClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheEntitiesAreDefinedInsideModelPackage() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        final Set<BeanDefinition> allEntityClasses = provider.findCandidateComponents(BASE_PACKAGE);
        final Set<BeanDefinition> entityClassesInModelPackage = provider.findCandidateComponents(BASE_PACKAGE + ".persistence.model");

        assertThat(entityClassesInModelPackage.isEmpty(), is(false));
        assertThat(entityClassesInModelPackage.size(), is(allEntityClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheConfigurationsAreDefinedInsideSpringPackage() {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Configuration.class));
        provider.addExcludeFilter(new AnnotationTypeFilter(SpringBootApplication.class));
        final Set<BeanDefinition> allConfigurationClasses = provider.findCandidateComponents(BASE_PACKAGE);
        final Set<BeanDefinition> configurationClassesInSpringPackage = provider.findCandidateComponents(BASE_PACKAGE + ".spring");

        assertThat(configurationClassesInSpringPackage.isEmpty(), is(false));
        assertThat(configurationClassesInSpringPackage.size(), is(allConfigurationClasses.size()));
    }
}