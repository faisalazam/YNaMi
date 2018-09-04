package ut.pk.lucidxpo.ynami;

import org.fluentlenium.core.FluentPage;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getAllTypes;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getAllTypesWithExclusions;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith;

public class PackageVerifierTest {
    public static final String BASE_PACKAGE = "pk.lucidxpo.ynami";
    private static final String UNIT_TEST_BASE_PACKAGE = "ut." + BASE_PACKAGE;
    private static final String INTEGRATION_TEST_BASE_PACKAGE = "it." + BASE_PACKAGE;
    private static final String MIGRATION_BASE_PACKAGE = "migration." + BASE_PACKAGE;
    static final String ACCEPTANCE_BASE_PACKAGE = "acceptance." + BASE_PACKAGE;
    static final String[] BASE_PACKAGES = new String[]{
            BASE_PACKAGE, ACCEPTANCE_BASE_PACKAGE, MIGRATION_BASE_PACKAGE, UNIT_TEST_BASE_PACKAGE, INTEGRATION_TEST_BASE_PACKAGE
    };

    @Test
    void shouldVerifyThatAllTheControllersAreDefinedInsideControllerPackage() {
        final Set<Class<?>> allControllerClasses = getTypesAnnotatedWith(Controller.class, BASE_PACKAGE);
        final Set<Class<?>> controllerClassesInControllerPackage = getTypesAnnotatedWith(Controller.class, BASE_PACKAGE + ".controller");

        assertThat(controllerClassesInControllerPackage.isEmpty(), is(false));
        assertThat(controllerClassesInControllerPackage.size(), is(allControllerClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheRepositoriesAreDefinedInsideDaoPackage() {
        final Set<Class<?>> allRepositoryClasses = getTypesAnnotatedWith(Repository.class, BASE_PACKAGE);
        final Set<Class<?>> repositoryClassesInDaoPackage = getTypesAnnotatedWith(Repository.class, BASE_PACKAGE + ".persistence.dao");

        assertThat(repositoryClassesInDaoPackage.isEmpty(), is(false));
        assertThat(repositoryClassesInDaoPackage.size(), is(allRepositoryClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheDtosAreDefinedInsideDtoPackage() {
        final Set<String> allDtoClasses = getAllTypes(".*DTO.class$", BASE_PACKAGES);
        final Set<String> dtoClassesInDtoPackage = getAllTypes(".*DTO.class$", BASE_PACKAGE + ".persistence.dto");

        assertThat(dtoClassesInDtoPackage.isEmpty(), is(false));
        assertThat(dtoClassesInDtoPackage.size(), is(allDtoClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheEntityBuildersAreDefinedInsideBuilderPackage() {
        final Set<String> allBuilderClasses = getAllTypesWithExclusions(".*Builder.class$", ".*DTOBuilder.class$", BASE_PACKAGES);
        final Set<String> builderClassesInBuilderPackage = getAllTypesWithExclusions(".*Builder.class$", ".*DTOBuilder.class$", BASE_PACKAGE + ".persistence.model");

        assertThat(builderClassesInBuilderPackage.isEmpty(), is(false));
        assertThat(builderClassesInBuilderPackage.size(), is(allBuilderClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheServicesAreDefinedInsideServicePackage() {
        final Set<Class<?>> allServiceClasses = getTypesAnnotatedWith(Service.class, BASE_PACKAGES);
        final Set<Class<?>> serviceClassesInServicePackage = getTypesAnnotatedWith(Service.class, BASE_PACKAGE + ".service");

        assertThat(serviceClassesInServicePackage.isEmpty(), is(false));
        assertThat(serviceClassesInServicePackage.size(), is(allServiceClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheEntitiesAreDefinedInsideModelPackage() {
        final Set<Class<?>> allEntityClasses = getTypesAnnotatedWith(Entity.class, BASE_PACKAGES);
        final Set<Class<?>> entityClassesInModelPackage = getTypesAnnotatedWith(Entity.class, BASE_PACKAGE + ".persistence.model");

        assertThat(entityClassesInModelPackage.isEmpty(), is(false));
        assertThat(entityClassesInModelPackage.size(), is(allEntityClasses.size()));
    }

    @Test
    void shouldVerifyThatAllTheConfigurationsAreDefinedInsideSpringPackage() {
        final Set<Class<?>> allConfigurationClasses = getTypesAnnotatedWith(Configuration.class, BASE_PACKAGES);
        final Set<Class<?>> configurationClassesInSpringPackage = getTypesAnnotatedWith(Configuration.class, BASE_PACKAGE + ".spring");

        assertThat(configurationClassesInSpringPackage.isEmpty(), is(false));
        assertThat(configurationClassesInSpringPackage.size(), is(allConfigurationClasses.size()));
    }

    @Test
    void shouldVerifyThatAllThePageObjectsAreDefinedInsidePageObjectsPackage() {
        final Set<Class<? extends FluentPage>> allFluentPages = new Reflections(
                new ConfigurationBuilder().forPackages(BASE_PACKAGES)
        ).getSubTypesOf(FluentPage.class);
        final Set<Class<? extends FluentPage>> fluentPagesInPageObjectPackage = new Reflections(ACCEPTANCE_BASE_PACKAGE + ".pageobjects").getSubTypesOf(FluentPage.class);

        assertThat(fluentPagesInPageObjectPackage.isEmpty(), is(false));
        assertThat(fluentPagesInPageObjectPackage.size(), is(allFluentPages.size()));
    }
}