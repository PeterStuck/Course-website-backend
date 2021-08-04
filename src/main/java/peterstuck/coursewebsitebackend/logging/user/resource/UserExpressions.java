package peterstuck.coursewebsitebackend.logging.user.resource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
class UserExpressions {

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.user.UserResource.registerNewUser(..))")
    public void forUserRegistration() {}

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.user.UserResource.getUserInfo(..))")
    public void forUserInfo() {}

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.user.UserResource.updateUser(..))")
    public void forUserUpdate() {}

    @Pointcut("forUserInfo() || forUserUpdate()")
    public void forUserInfoAndUpdate() {};

}
