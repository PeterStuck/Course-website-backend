package peterstuck.coursewebsitebackend.utils;

import org.hibernate.Hibernate;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.models.user.User;

/**
 * Initializes object collections and inner objects with Hibernate
 */
public class ObjectInitializer {

    public static void initializeCourseObject(Course course) {
        Hibernate.initialize(course.getSubtitles());
        Hibernate.initialize(course.getCategories());
        Hibernate.initialize(course.getAuthors());
        Hibernate.initialize(course.getCourseFeedback().getComments());

        course.getAuthors().forEach(ObjectInitializer::initializeUserObject);

        if (course.getCourseDescription() != null) {
            Hibernate.initialize(course.getCourseDescription());
            Hibernate.initialize(course.getCourseDescription().getMainTopics());
            Hibernate.initialize(course.getCourseDescription().getRequirements());
        }
    }

    public static void initializeUserObject(User user) {
        Hibernate.initialize(user.getPurchasedCourses());
        Hibernate.initialize(user.getOwnCourses());
        Hibernate.initialize(user.getUserDetail());
        Hibernate.initialize(user.getUserActivity());

        user.getPurchasedCourses().forEach(ObjectInitializer::initializeCourseObject);
    }

}
