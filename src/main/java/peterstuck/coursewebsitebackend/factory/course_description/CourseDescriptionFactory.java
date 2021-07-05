package peterstuck.coursewebsitebackend.factory.course_description;

import peterstuck.coursewebsitebackend.builder.course_description.CourseDescriptionBuilder;
import peterstuck.coursewebsitebackend.builder.course_description.StandardCourseDescriptionBuilder;
import peterstuck.coursewebsitebackend.models.CourseDescription;

import java.util.List;

public class CourseDescriptionFactory {

    private static CourseDescriptionBuilder builder = new StandardCourseDescriptionBuilder();

    public static CourseDescription createCourseDescription(Double duration, String shortDescription) {
        CourseDescription cd = builder
                .buildDuration(duration)
                .buildShortDescription(shortDescription)
                .getResult();
        builder.restore();
        return cd;
    }

    public static CourseDescription createCourseDescription(Double duration, String shortDescription, String longDescription) {
        CourseDescription cd = builder
                .buildDuration(duration)
                .buildShortDescription(shortDescription)
                .buildLongDescription(longDescription)
                .getResult();
        builder.restore();
        return cd;
    }

    public static CourseDescription createCourseDescription(
            Double duration,
            String shortDescription,
            String longDescription,
            List<String> topics,
            List<String> requirements) {
        CourseDescription cd = builder
                .buildDuration(duration)
                .buildShortDescription(shortDescription)
                .buildLongDescription(longDescription)
                .buildMainTopics(topics)
                .buildRequirements(requirements)
                .getResult();
        builder.restore();
        return cd;
    }

    public static void setBuilder(CourseDescriptionBuilder builder) {
        CourseDescriptionFactory.builder = builder;
    }
}
