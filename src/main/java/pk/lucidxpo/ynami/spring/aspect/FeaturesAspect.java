package pk.lucidxpo.ynami.spring.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrapper;

@Slf4j
@Aspect
@Component
public class FeaturesAspect {
    @Autowired
    private FeatureManagerWrapper featureManager;

    @Around(
            "@within(featureAssociation) || @annotation(featureAssociation)"
    )
    public Object checkAspect(final ProceedingJoinPoint joinPoint,
                              final FeatureAssociation featureAssociation) throws Throwable {
        if (featureManager.isActive(featureAssociation.value())) {
            return joinPoint.proceed();
        }
        log.info("Feature " + featureAssociation.value().name() + " is not enabled!");
        return null;
    }
}