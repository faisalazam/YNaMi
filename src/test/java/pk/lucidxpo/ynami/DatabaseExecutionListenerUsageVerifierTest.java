package pk.lucidxpo.ynami;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.togglz.core.Feature;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;

class DatabaseExecutionListenerUsageVerifierTest {

    @Test
    void shouldVerifyThatIntegrationTestsChangingStateOfFeatureTogglesHaveDatabaseExecutionListenerAsTheirTestExecutionListeners() throws Exception {
        final Method activatedMethod = FeatureManagerWrappable.class.getDeclaredMethod("activate", Feature.class);
        final Method deactivatedMethod = FeatureManagerWrappable.class.getDeclaredMethod("deactivate", Feature.class);

        final Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(BASE_PACKAGE)
                        .setScanners(new MemberUsageScanner())
                        .filterInputsBy(className -> requireNonNull(className).endsWith("IntegrationTest.class"))
        );
        final Set<Member> usages = reflections.getMethodUsage(activatedMethod);
        usages.addAll(reflections.getMethodUsage(deactivatedMethod));

        assertThat("The list of integration test classes changing the feature toggles' state must not be empty", usages.isEmpty(), is(false));
        usages.stream().map(Member::getDeclaringClass).forEach(this::assertDatabaseExecutionListenerUsage);
    }

    @Test
    void shouldVerifyThatIntegrationTestsAnnotatedWithSqlHaveDatabaseExecutionListenerAsTheirTestExecutionListeners() {
        final Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(BASE_PACKAGE)
                        .filterInputsBy(className -> requireNonNull(className).endsWith("IntegrationTest.class"))
        );
        final Set<Class<?>> testClassesAnnotatedWithSql = reflections.getTypesAnnotatedWith(Sql.class);

        assertThat("The list of integration test classes annotated with @Sql must not be empty", testClassesAnnotatedWithSql.isEmpty(), is(false));
        testClassesAnnotatedWithSql.forEach(this::assertDatabaseExecutionListenerUsage);
    }

    private void assertDatabaseExecutionListenerUsage(final Class<?> testClazz) {
        final TestExecutionListeners testExecutionListeners = findAnnotation(testClazz, TestExecutionListeners.class);
        assertNotNull(testClazz.getSimpleName() + " should be annotated with " + TestExecutionListeners.class.getSimpleName(), testExecutionListeners);
        assertThat(testExecutionListeners.listeners(), hasItemInArray(DatabaseExecutionListener.class));
    }
}