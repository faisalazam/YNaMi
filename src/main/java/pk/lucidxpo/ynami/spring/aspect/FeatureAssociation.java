package pk.lucidxpo.ynami.spring.aspect;

import pk.lucidxpo.ynami.spring.features.FeatureToggles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD})
public @interface FeatureAssociation {
    FeatureToggles value();
}