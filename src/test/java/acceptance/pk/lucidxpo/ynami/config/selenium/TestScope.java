package acceptance.pk.lucidxpo.ynami.config.selenium;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.synchronizedMap;

/*
 * The test scope implementation is trivial. It holds a map where test scoped beans are cached,
 * ensuring that only one instance is created. It also has a reset method for clearing the cache before each test run.
 */
@SuppressWarnings("NullableProblems")
class TestScope implements Scope {

    private final Map<String, Object> cache = synchronizedMap(newHashMap());
    private final Map<String, Runnable> destructionCallbacks = synchronizedMap(newHashMap());

    void reset() {
        cache.clear();
    }

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