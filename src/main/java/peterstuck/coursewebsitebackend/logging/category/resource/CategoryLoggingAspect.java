package peterstuck.coursewebsitebackend.logging.category.resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import peterstuck.coursewebsitebackend.logging.LoggingUtil;

@Profile("!test")
@Aspect
@Component
public class CategoryLoggingAspect {

    @Autowired
    private LoggingUtil loggingUtil;

    @Around("peterstuck.coursewebsitebackend.logging.category.resource.CategoryExpressions.forAddUpdateAndDeleteCategory()")
    public Object aroundAddUpdateDeleteCategory(ProceedingJoinPoint joinPoint) throws Throwable {
        String userEmail = loggingUtil.getUserEmailFromRequest(joinPoint.getArgs()[0]);

        loggingUtil.logger().info("User: " + userEmail + " call " + joinPoint.getSignature().toShortString() +
                " with args: " + loggingUtil.convertArgsToStringWithoutJWT(joinPoint.getArgs(), 0));

        Object result = joinPoint.proceed();

        loggingUtil.logger().info("Operation: " + joinPoint.getSignature().toShortString() + " completed successfully.");

        return result;
    }

    @AfterThrowing(pointcut = "peterstuck.coursewebsitebackend.logging.category.resource.CategoryExpressions.forAddUpdateAndDeleteCategory()", throwing = "ex")
    public void afterThrowingAddUpdateDeleteCategory(Throwable ex) {
        loggingUtil.logger().error(ex.getMessage());
    }

}
