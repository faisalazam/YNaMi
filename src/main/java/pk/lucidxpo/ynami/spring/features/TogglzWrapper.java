package pk.lucidxpo.ynami.spring.features;

import org.springframework.context.ApplicationContext;

import static org.togglz.spring.util.ContextClassLoaderApplicationContextHolder.bind;
import static org.togglz.spring.util.ContextClassLoaderApplicationContextHolder.get;
import static org.togglz.spring.util.ContextClassLoaderApplicationContextHolder.release;

public class TogglzWrapper {
    public static ApplicationContext getApplicationContext() {
        return get();
    }

    public static void bindApplicationContext(final ApplicationContext context) {
        bind(context);
    }

    public static void releaseApplicationContext() {
        release();
    }
}