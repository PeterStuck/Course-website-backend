package peterstuck.coursewebsitebackend.logging.course.resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import peterstuck.coursewebsitebackend.logging.LoggingUtil;
import peterstuck.coursewebsitebackend.models.course.Course;

@Profile("!test")
@Aspect
@Component
public class CourseLoggingAspect {

    @Autowired
    private LoggingUtil loggingUtil;

    @AfterReturning(pointcut = "peterstuck.coursewebsitebackend.logging.course.resource.CourseExpressions.forCourseCreate()", returning = "course")
    public void afterAddCourse(JoinPoint joinPoint, Course course) {
        loggingUtil.logger().info(
                getInvoker(joinPoint) + " created course: " + course.getTitle()
        );
    }

    @AfterReturning(pointcut = "peterstuck.coursewebsitebackend.logging.course.resource.CourseExpressions.forCourseUpdate()", returning = "course")
    public void afterUpdateCourse(JoinPoint joinPoint, Course course) {
        loggingUtil.logger().info(
                getInvoker(joinPoint) + " updated course: " + course.getTitle()
        );
    }

    @AfterReturning(pointcut = "peterstuck.coursewebsitebackend.logging.course.resource.CourseExpressions.forCourseDelete()")
    public void afterDeleteCourse(JoinPoint joinPoint) {
        loggingUtil.logger().info(
               getInvoker(joinPoint)  + " deleted course with ID: " + joinPoint.getArgs()[1]
        );
    }

    private String getInvoker(JoinPoint joinPoint) {
        return "User: " + loggingUtil.getUserEmailFromRequest(joinPoint.getArgs()[0]);
    }

    @AfterThrowing(pointcut = "peterstuck.coursewebsitebackend.logging.course.resource.CourseExpressions.forCreateUpdateDeleteCourse()", throwing = "ex")
    public void afterThrowingAddUpdateDeleteCourse(Throwable ex) {
        loggingUtil.logger().error(ex.getMessage());
    }

}

