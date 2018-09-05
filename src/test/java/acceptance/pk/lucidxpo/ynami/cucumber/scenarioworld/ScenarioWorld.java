package acceptance.pk.lucidxpo.ynami.cucumber.scenarioworld;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Component
@Target(TYPE)
@Retention(RUNTIME)
@Scope("cucumber-glue")
@interface ScenarioWorld {
}
