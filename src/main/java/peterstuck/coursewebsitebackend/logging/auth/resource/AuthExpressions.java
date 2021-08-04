package peterstuck.coursewebsitebackend.logging.auth.resource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
class AuthExpressions {

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.auth.AuthResource.*(..))")
    public void forUserAuthentication() {}

}
