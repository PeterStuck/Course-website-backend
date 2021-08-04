package peterstuck.coursewebsitebackend.logging.course.resource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
class CourseExpressions {

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.course.CourseResource.addCourse(..))")
    public void forCourseCreate() {}

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.course.CourseResource.updateCourse(..))")
    public void forCourseUpdate() {}

    @Pointcut("execution(* peterstuck.coursewebsitebackend.resources.course.CourseResource.deleteCourse(..))")
    public void forCourseDelete() {}

    @Pointcut("forCourseCreate() || forCourseUpdate() || forCourseDelete()")
    public void forCreateUpdateDeleteCourse() {}

}
