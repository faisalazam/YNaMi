package acceptance.pk.lucidxpo.ynami.cucumber.annotations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * By default, Spring creates all singleton beans eagerly at the startup/bootstrapping of the application context.
 * The reason behind this is simple: to avoid and detect all possible errors immediately rather than at runtime.
 * <p>
 * However, there are cases when we need to create a bean, not at the application context startup, but when we request it.
 * <p>
 * Hence the use of the {@link Lazy} annotation.
 * <p>
 * When we put {@link Lazy} annotation over the {@link Configuration} class, it indicates that all the methods with
 * {@link Bean} annotation should be loaded lazily. So all beans will be created only when we request them for the
 * first time.
 * <p>
 * Here we are creating our own combined @LazyConfiguration annotation to achieve the same result.
 */
@Lazy
@Documented
@Configuration
@Target({TYPE})
@Retention(RUNTIME)
public @interface LazyConfiguration {
}
