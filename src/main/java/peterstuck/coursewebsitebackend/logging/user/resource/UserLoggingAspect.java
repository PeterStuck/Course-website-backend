package peterstuck.coursewebsitebackend.logging.user.resource;

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
public class UserLoggingAspect {

    @Autowired
    private LoggingUtil loggingUtil;

    @Around("peterstuck.coursewebsitebackend.logging.user.resource.UserExpressions.forUserRegistration()")
    public Object aroundUserRegister(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        loggingUtil.logger().info(joinPoint.getArgs()[0] + " successfully registered.");

        return result;
    }

    @AfterThrowing(pointcut = "peterstuck.coursewebsitebackend.logging.user.resource.UserExpressions.forUserRegistration()", throwing="exception")
    public void afterThrowingUserRegister(Throwable exception) {
        loggingUtil.logger().error(exception.getMessage());
    }

    @Around("peterstuck.coursewebsitebackend.logging.user.resource.UserExpressions.forUserInfoAndUpdate()")
    public Object aroundUserUpdateOrGetUserInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        String extractedEmail = loggingUtil.getUserEmailFromRequest(joinPoint.getArgs()[0]);
        loggingUtil.logger().info("""
            User with email: %s invoked %s """.formatted(extractedEmail, joinPoint.getSignature().toShortString()));
        Object result = joinPoint.proceed();
        loggingUtil.logger().info("Operation completed successfully.");

        return result;
    }

    @AfterThrowing(pointcut = "peterstuck.coursewebsitebackend.logging.user.resource.UserExpressions.forUserInfoAndUpdate()", throwing="exception")
    public void afterUserUpdateOrGetUserInfo(Throwable exception) {
        loggingUtil.logger().error(exception.getMessage());
    }

}
