package migration.pk.lucidxpo.ynami.helper;

import jakarta.persistence.Entity;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith;
import static ut.pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;

public class ReflectionHelper {
    public static Collection<Class<?>> getEntityClasses(Collection<Class<?>> entityClasses) {
        return getTypesAnnotatedWith(Entity.class, BASE_PACKAGE)
                .stream()
                .filter(entityClass -> !entityClasses.contains(entityClass))
                .collect(toSet());
    }
}
