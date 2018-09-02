package pk.lucidxpo.ynami;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import pk.lucidxpo.ynami.acceptance.config.selenium.SeleniumTestCaseContext;
import pk.lucidxpo.ynami.acceptance.pageobjects.PageObject;

import javax.persistence.Entity;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        allConfigurationClasses.remove(SeleniumTestCaseContext.class);

        final Set<Class<?>> configurationClassesInSpringPackage = getTypesAnnotatedWith(BASE_PACKAGE + ".spring", Configuration.class);

        assertThat(configurationClassesInSpringPackage.isEmpty(), is(false));
        assertThat(configurationClassesInSpringPackage.size(), is(allConfigurationClasses.size()));
    }

    @Test
    void shouldVerifyThatAllThePageObjectsAreDefinedInsidePageObjectsPackage() {
        final Set<Class<?>> allPageObjectClasses = getTypesAnnotatedWith(BASE_PACKAGE, PageObject.class);
        final Set<Class<?>> pageObjectClasses = getTypesAnnotatedWith(BASE_PACKAGE + ".acceptance.pageobjects", PageObject.class);


        assertThat(pageObjectClasses.isEmpty(), is(false));
        assertThat(pageObjectClasses.size(), is(allPageObjectClasses.size()));
    }

    @Test
    void shouldVerifyThatAllThePageObjectsAreAnnotatedWithPageObject() {
        final Set<Class<?>> allPageObjectClasses = getTypesAnnotatedWith(BASE_PACKAGE, PageObject.class);
        final Set<String> classes = getAllTypes(".*Page.class$", BASE_PACKAGE);
        classes.remove("pk.lucidxpo.ynami.acceptance.pageobjects.BasePage");

        assertThat(classes.isEmpty(), is(false));
        assertThat(classes.size(), is(allPageObjectClasses.size()));

        for (final Class<?> pageObjectClass : allPageObjectClasses) {
            assertTrue(classes.contains(pageObjectClass.getName()));
        }
    }
}