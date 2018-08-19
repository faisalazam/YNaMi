package pk.lucidxpo.ynami;

import org.junit.jupiter.api.Test;
import pk.lucidxpo.ynami.utils.ReflectionHelper;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.reflections.ReflectionUtils.getSuperTypes;
import static pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;

class ContextConfigurationExtendsVerifierTest {

    @Test
    void shouldVerifyThatAllTheIntegrationTestsAreExtendedFromAbstractIntegrationTest() throws Exception {
        final Set<String> classes = ReflectionHelper.getAllTypes(".*IntegrationTest.class$", BASE_PACKAGE);

        //The integrations test classes that don't require application context, should not be extended from "AbstractIntegrationTest".
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
}