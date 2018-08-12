package pk.lucidxpo.ynami;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.util.regex.Pattern.compile;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ContextConfigurationExtendsVerifierTest {

    @Test
    public void shouldVerifyThatAllTheIntegrationTestsAreExtendedFromAbstractIntegrationTest() throws Exception {

        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(compile(".*IntegrationTest$")));
        final Set<BeanDefinition> classes = provider.findCandidateComponents("pk.lucidxpo.ynami");

        //The integrations test classes that don't require application context, should not be extended from "AbstractIntegrationTest".
        final Set<String> excludedClassNames = newHashSet(
                AbstractIntegrationTest.class.getName()
        );

        assertThat("The list of integration test classes having name ending with 'IntegrationTest' must not be empty", classes.isEmpty(), is(false));
        assertThat(classes.size() > excludedClassNames.size(), is(true));

        for (BeanDefinition bean : classes) {
            final Class<?> testClazz = forName(bean.getBeanClassName());
            if (excludedClassNames.contains(bean.getBeanClassName())) {
                assertThat(bean.getBeanClassName() + " should not be extended from " + AbstractIntegrationTest.class.getSimpleName() + ".",
                        testClazz.getSuperclass().getName(), not(AbstractIntegrationTest.class.getName())
                );
            } else {
                assertThat(bean.getBeanClassName() + " should be extended from " + AbstractIntegrationTest.class.getSimpleName() + ".",
                        testClazz.getSuperclass().getName(), is(AbstractIntegrationTest.class.getName())
                );
            }
        }
    }
}
