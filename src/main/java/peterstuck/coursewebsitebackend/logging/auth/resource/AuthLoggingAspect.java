package peterstuck.coursewebsitebackend.logging.auth.resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import peterstuck.coursewebsitebackend.logging.LoggingUtil;
import peterstuck.coursewebsitebackend.resources.auth.UserCredentials;

@Profile("!test")
@Aspect
@Component
public class AuthLoggingAspect {

    @Autowired
    private LoggingUtil loggingUtil;

    @Around("peterstuck.coursewebsitebackend.logging.auth.resource.AuthExpressions.forUserAuthentication()")
    public Object aroundUserAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {
        UserCredentials credentials = (UserCredentials) joinPoint.getArgs()[0];
        loggingUtil.logger().info("Authenticating user: " + credentials.getEmail() + "...");

        Object result = joinPoint.proceed();

        loggingUtil.logger().info("User authenticated. Generating JWT...");

        return result;
    }

    @AfterThrowing(pointcut = "peterstuck.coursewebsitebackend.logging.auth.resource.AuthExpressions.forUserAuthentication()", throwing = "exception")
    public void afterThrowingUserAuthentication(Throwable exception) {
        loggingUtil.logger().error(exception.getMessage());
    }

}
