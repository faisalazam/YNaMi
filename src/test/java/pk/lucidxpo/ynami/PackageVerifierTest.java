package pk.lucidxpo.ynami;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getAllTypes;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith;

public class PackageVerifierTest {
    public static final String BASE_PACKAGE = "pk.lucidxpo.ynami";

    @Test
    void shouldVerifyThatAllTheControllersAreDefinedInsideControllerPackage() {
        final Set<Class<?>> allControllerClasses = getTypesAnnotatedWith(BASE_PACKAGE, Controller.class);
        final Set<Class<?>> controllerClassesInControllerPackage = getTypesAnnotatedWith(BASE_PACKAGE + ".controller", Controller.class);

        assertThat(controllerClassesInControllerPackage.isEmpty(), is(false));
        assertThat(controllerClassesInControllerPackage.size(), is(allControllerClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheRepositoriesAreDefinedInsideDaoPackage() {
        final Set<Class<?>> allRepositoryClasses = getTypesAnnotatedWith(BASE_PACKAGE, Repository.class);
        final Set<Class<?>> repositoryClassesInDaoPackage = getTypesAnnotatedWith(BASE_PACKAGE + ".persistence.dao", Repository.class);

        assertThat(repositoryClassesInDaoPackage.isEmpty(), is(false));
        assertThat(repositoryClassesInDaoPackage.size(), is(allRepositoryClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheDtosAreDefinedInsideDtoPackage() {
        final Set<String> allDtoClasses = getAllTypes(".*DTO.class$", BASE_PACKAGE);
        final Set<String> dtoClassesInDtoPackage = getAllTypes(".*DTO.class$", BASE_PACKAGE + ".persistence.dto");

        assertThat(dtoClassesInDtoPackage.isEmpty(), is(false));
        assertThat(dtoClassesInDtoPackage.size(), is(allDtoClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheEntityBuildersAreDefinedInsideBuilderPackage() {
        final Set<String> allBuilderClasses = getAllTypes(".*Builder.class$", ".*DTOBuilder.class$", BASE_PACKAGE);
        final Set<String> builderClassesInBuilderPackage = getAllTypes(".*Builder.class$", ".*DTOBuilder.class$", BASE_PACKAGE + ".persistence.model");

        assertThat(builderClassesInBuilderPackage.isEmpty(), is(false));
        assertThat(builderClassesInBuilderPackage.size(), is(allBuilderClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheServicesAreDefinedInsideServicePackage() {
        final Set<Class<?>> allServiceClasses = getTypesAnnotatedWith(BASE_PACKAGE, Service.class);
        final Set<Class<?>> serviceClassesInServicePackage = getTypesAnnotatedWith(BASE_PACKAGE + ".service", Service.class);

        assertThat(serviceClassesInServicePackage.isEmpty(), is(false));
        assertThat(serviceClassesInServicePackage.size(), is(allServiceClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheEntitiesAreDefinedInsideModelPackage() {
        final Set<Class<?>> allEntityClasses = getTypesAnnotatedWith(BASE_PACKAGE, Entity.class);
        final Set<Class<?>> entityClassesInModelPackage = getTypesAnnotatedWith(BASE_PACKAGE + ".persistence.model", Entity.class);

        assertThat(entityClassesInModelPackage.isEmpty(), is(false));
        assertThat(entityClassesInModelPackage.size(), is(allEntityClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheConfigurationsAreDefinedInsideSpringPackage() {
        final Set<Class<?>> allConfigurationClasses = getTypesAnnotatedWith(BASE_PACKAGE, Configuration.class);
        final Set<Class<?>> configurationClassesInSpringPackage = getTypesAnnotatedWith(BASE_PACKAGE + ".spring", Configuration.class);

        assertThat(configurationClassesInSpringPackage.isEmpty(), is(false));
        assertThat(configurationClassesInSpringPackage.size(), is(allConfigurationClasses.size()));
    }
}