package pk.lucidxpo.ynami;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static org.springframework.boot.SpringApplication.run;
import static org.togglz.spring.util.ContextClassLoaderApplicationContextHolder.bind;
import static org.togglz.spring.util.ContextClassLoaderApplicationContextHolder.get;
import static org.togglz.spring.util.ContextClassLoaderApplicationContextHolder.release;

@SpringBootApplication
public class YNaMiApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        run(YNaMiApplication.class, args);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        if (get() != null) {
            release();
        }
        bind(applicationContext);
    }
}
