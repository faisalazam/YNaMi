package acceptance.pk.lucidxpo.ynami.cucumber.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * By default, Spring creates all singleton beans eagerly at the startup/bootstrapping of the application context.
 * The reason behind this is simple: to avoid and detect all possible errors immediately rather than at runtime.
 * <p>
 * However, there are cases when we need to create a bean, not at the application context startup, but when we request it.
 * <p>
 * Hence the use of the {@link Lazy} annotation. In order to initialize a lazy bean, we reference it from another one
 * with both {@link Lazy} and {@link Autowired} annotations and the bean itself is marked with both {@link Lazy} and
 * {@link Component} annotations.
 * <p>
 * Here we are creating our own combined @LazyAutowired annotation to achieve the same result.
 */
@Lazy
@Autowired
@Documented
@Target({FIELD})
@Retention(RUNTIME)
public @interface LazyAutowired {
}
