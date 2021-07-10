package peterstuck.coursewebsitebackend.builder.course;

import peterstuck.coursewebsitebackend.models.course.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class StandardCourseBuilder implements CourseBuilder {

    protected Course course;

    public StandardCourseBuilder() {
        this.course = new Course();
    }

    @Override
    public CourseBuilder buildTitle(String title) {
        course.setTitle(title);

        return this;
    }

    @Override
    public CourseBuilder buildRates(List<Rate> rates) {
        course.getCourseFeedback().setRates(rates);

        return this;
    }

    @Override
    public CourseBuilder buildLanguages(Set<Language> languages) {
        course.setLanguages(languages);

        return this;
    }

    @Override
    public CourseBuilder buildSubtitles(Set<Language> subtitles) {
        course.setSubtitles(subtitles);

        return this;
    }

    @Override
    public CourseBuilder buildCategories(List<Category> categories) {
        course.setCategories(categories);

        return this;
    }

    @Override
    public CourseBuilder buildComments(List<Comment> comments) {
        course.getCourseFeedback().setComments(comments);

        return this;
    }

    @Override
    public CourseBuilder buildLastUpdate(Long lastUpdate) {
        course.setLastUpdate(new Date().getTime());

        return this;
    }

    @Override
    public CourseBuilder buildPrice(Double price) {
        course.setPrice(price);

        return this;
    }

    @Override
    public CourseBuilder buildCourseDescription(CourseDescription description) {
        course.setCourseDescription(description);

        return this;
    }

    @Override
    public CourseBuilder buildCourseFeedback(CourseFeedback courseFeedback) {
        course.setCourseFeedback(courseFeedback);

        return this;
    }

    @Override
    public Course getResult() {
        return this.course;
    }

    @Override
    public void restore() {
        this.course = new Course();
    }

}
