package ut.pk.lucidxpo.ynami;

import acceptance.pk.lucidxpo.ynami.config.selenium.AbstractSeleniumTest;
import org.fluentlenium.adapter.junit.jupiter.FluentTest;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import it.pk.lucidxpo.ynami.AbstractIntegrationTest;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.reflections.ReflectionUtils.getSuperTypes;
import static ut.pk.lucidxpo.ynami.PackageVerifierTest.ACCEPTANCE_BASE_PACKAGE;
import static ut.pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGES;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getAllTypes;

class ContextConfigurationExtendsVerifierTest {

    @Test
    void shouldVerifyThatAllTheIntegrationTestsAreExtendedFromAbstractIntegrationTest() throws Exception {
        final Set<String> classes = getAllTypes(".*IntegrationTest.class$", BASE_PACKAGES);

        //The integration test classes that don't require application context, should not be extended from "AbstractIntegrationTest".
        final Set<String> excludedClassNames = newHashSet(
                AbstractIntegrationTest.class.getName()
        );

        assertThat("The list of integration test classes having name ending with 'IntegrationTest' must not be empty", classes.isEmpty(), is(false));
        assertThat(classes.size() > excludedClassNames.size(), is(true));

        for (String className : classes) {
            final Class<?> testClazz = forName(className);
            if (excludedClassNames.contains(className)) {
                assertThat(className + " should not be extended from " + AbstractIntegrationTest.class.getSimpleName() + ".",
                        getSuperTypes(testClazz), not(hasItem(AbstractIntegrationTest.class))
                );
            } else {
                assertThat(className + " should be extended from " + AbstractIntegrationTest.class.getSimpleName() + ".",
                        getSuperTypes(testClazz), hasItem(AbstractIntegrationTest.class)
                );
            }
        }
    }

    @Test
    void shouldVerifyThatAllTheIntegrationTestsAreExtendedFromFluentTest() throws Exception {
        final Set<Class<? extends FluentTest>> allFluentTests = new Reflections(
                new ConfigurationBuilder().forPackages(BASE_PACKAGES)
        ).getSubTypesOf(FluentTest.class);
        final Set<Class<? extends FluentTest>> fluentTestsInSeleniumTestPackage = new Reflections(ACCEPTANCE_BASE_PACKAGE + ".selenium.test").getSubTypesOf(FluentTest.class);

        //The selenium test classes that don't require application context, should not be extended from "AbstractSeleniumTest".
        final Set<String> excludedClassNames = newHashSet(
                AbstractSeleniumTest.class.getName()
        );

        assertThat("The list of selenium test classes having name ending with 'SeleniumTest' must not be empty", allFluentTests.isEmpty(), is(false));
        assertThat(allFluentTests.size(), is(fluentTestsInSeleniumTestPackage.size()));
        assertThat(allFluentTests.size() > excludedClassNames.size(), is(true));

        for (final Class<? extends FluentTest> clazz : allFluentTests) {
            final String className = clazz.getName();
            assertThat(className, endsWith("SeleniumTest"));

            final Class<?> testClazz = forName(className);
            if (excludedClassNames.contains(className)) {
                assertThat(className + " should not be extended from " + AbstractSeleniumTest.class.getSimpleName() + ".",
                        getSuperTypes(testClazz), not(hasItem(AbstractSeleniumTest.class))
                );
            } else {
                assertThat(className + " should be extended from " + AbstractSeleniumTest.class.getSimpleName() + ".",
                        getSuperTypes(testClazz), hasItem(AbstractSeleniumTest.class)
                );
            }
        }
    }
}