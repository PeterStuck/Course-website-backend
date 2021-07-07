package peterstuck.coursewebsitebackend.builder.course_description;

import peterstuck.coursewebsitebackend.models.course.CourseDescription;

import java.util.List;

public class StandardCourseDescriptionBuilder implements CourseDescriptionBuilder {

    private CourseDescription description;

    public StandardCourseDescriptionBuilder() {
        description = new CourseDescription();
    }

    @Override
    public CourseDescriptionBuilder buildDuration(Double duration) {
        description.setDuration(duration);

        return this;
    }

    @Override
    public CourseDescriptionBuilder buildShortDescription(String shortDesc) {
        description.setShortDescription(shortDesc);

        return this;
    }

    @Override
    public CourseDescriptionBuilder buildLongDescription(String longDesc) {
        description.setLongDescription(longDesc);

        return this;
    }

    @Override
    public CourseDescriptionBuilder buildMainTopics(List<String> topics) {
        description.setMainTopics(topics);

        return this;
    }

    @Override
    public CourseDescriptionBuilder buildRequirements(List<String> requirements) {
        description.setRequirements(requirements);

        return this;
    }

    @Override
    public CourseDescription getResult() {
        return description;
    }

    @Override
    public void restore() {
        description = new CourseDescription();
    }
}
