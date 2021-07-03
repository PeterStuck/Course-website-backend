package peterstuck.coursewebsitebackend.builder;

import peterstuck.coursewebsitebackend.models.*;

import java.util.Date;
import java.util.List;

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
    public CourseBuilder buildRates(List<Double> rates) {
        course.setRates(rates);

        return this;
    }

    @Override
    public CourseBuilder buildLanguages(List<Language> languages) {
        course.setLanguages(languages);

        return this;
    }

    @Override
    public CourseBuilder buildSubtitles(List<Language> subtitles) {
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
        course.setComments(comments);

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
    public Course getResult() {
        return this.course;
    }

    @Override
    public void restore() {
        this.course = new Course();
    }

}
