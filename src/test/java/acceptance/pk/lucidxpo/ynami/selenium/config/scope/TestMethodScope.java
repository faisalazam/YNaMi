package acceptance.pk.lucidxpo.ynami.selenium.config.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.synchronizedMap;

/**
 * Out of the box, Spring provides two standard bean scopes (“singleton” and “prototype”) that can be used in any
 * Spring application, plus three additional bean scopes (“request”, “session”, and “globalSession”) for use only
 * in web-aware applications.
 * <p>
 * But we need some scope just for the life of test method and hence the {@link TestMethodScopeBean}.
 * <p>
 * The test scope implementation is trivial. It holds a map where test scoped beans are cached,
 * ensuring that only one instance is created. It also has a reset method for clearing the cache before each test run.
 * <p>
 * It works in conjunction with {@link TestMethodScopeBean}, {@link TestMethodScope} and
 * {@link SeleniumTestExecutionListener} classes.
 * <p>
 * {@link TestMethodScopeBean} will mark the beans with {@link TestMethodScopeBean#TEST_METHOD_SCOPE},
 * {@link TestMethodScope} will act as a sort of repository for all the beans annotated with {@link TestMethodScopeBean},
 * and finally the {@link SeleniumTestExecutionListener} will be clearing those beans based on the lifecycle of the test.
 */
@SuppressWarnings("NullableProblems")
public class TestMethodScope implements Scope {
    /**
     * NOTE the use of {@link Collections#synchronizedMap}, that is to ensure that the implementation is thread-safe
     * because scopes can be used by multiple bean factories at the same time.
     */
    private final Map<String, Object> cache = synchronizedMap(newHashMap());
    private final Map<String, Runnable> destructionCallbacks = synchronizedMap(newHashMap());

    void reset() {
        cache.clear();
    }

    /**
     * This method checks to see if the named object is there in our map. If it is, we return it, and if not, we use
     * the ObjectFactory to create a new object, add it to our map, and return it.
     */
    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        if (!cache.containsKey(name)) {
            cache.put(name, objectFactory.getObject());
        }
        return cache.get(name);
    }

    public boolean contains(final String beanName) {
        return cache.containsKey(beanName);
    }

    @Override
    public Object remove(final String name) {
        return cache.remove(name);
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
        destructionCallbacks.put(name, callback);
    }

    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}