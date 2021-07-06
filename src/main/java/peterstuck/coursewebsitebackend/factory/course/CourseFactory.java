package peterstuck.coursewebsitebackend.factory.course;

import peterstuck.coursewebsitebackend.builder.course.CourseBuilder;
import peterstuck.coursewebsitebackend.builder.course.StandardCourseBuilder;
import peterstuck.coursewebsitebackend.models.Category;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.models.CourseDescription;
import peterstuck.coursewebsitebackend.models.Language;

import java.util.List;
import java.util.Set;

public class CourseFactory {

    private static CourseBuilder courseBuilder = new StandardCourseBuilder();

    public static Course createCourse(String title, Double price, CourseDescription description) {
        Course course = courseBuilder
                .buildTitle(title)
                .buildPrice(price)
                .buildCourseDescription(description)
                .getResult();
        courseBuilder.restore();

        return course;
    }

    public static Course createCourse(String title, Double price, CourseDescription description, List<Category> categories) {
        Course course = courseBuilder
                .buildTitle(title)
                .buildPrice(price)
                .buildCourseDescription(description)
                .buildCategories(categories)
                .getResult();
        courseBuilder.restore();

        return course;
    }

    public static Course createCourse(String title, Double price, CourseDescription description, Set<Language> languages, Set<Language> subtitles, List<Category> categories) {
        Course course = courseBuilder
                .buildTitle(title)
                .buildPrice(price)
                .buildCourseDescription(description)
                .buildLanguages(languages)
                .buildSubtitles(subtitles)
                .buildCategories(categories)
                .getResult();
        courseBuilder.restore();

        return course;
    }

    public static void setCourseBuilder(CourseBuilder builder) {
        courseBuilder = builder;
    }

}
