package pk.lucidxpo.ynami.spring.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

@Slf4j
@Aspect
@Component
public class FeaturesAspect {
    private final FeatureManagerWrappable featureManager;

    @Autowired
    public FeaturesAspect(final FeatureManagerWrappable featureManager) {
        this.featureManager = featureManager;
    }

    @Around(
            "@within(featureAssociation) || @annotation(featureAssociation)"
    )
    public Object checkAspect(final ProceedingJoinPoint joinPoint,
                              final FeatureAssociation featureAssociation) throws Throwable {
        if (featureManager.isActive(featureAssociation.value())) {
            return joinPoint.proceed();
        }
        final Signature signature = ((MethodInvocationProceedingJoinPoint) joinPoint).getSignature();
        log.info(
                "Execution of '" + signature.getName() + "' method in '" +
                        signature.getDeclaringTypeName() + "' class is blocked " +
                        "as the feature '" + featureAssociation.value() + "' is not enabled!"
        );
        return null;
    }
}