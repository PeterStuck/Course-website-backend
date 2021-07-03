package peterstuck.coursewebsitebackend.builder;

import peterstuck.coursewebsitebackend.models.*;

import java.util.List;

public interface CourseBuilder {

    CourseBuilder buildTitle(String title);

    CourseBuilder buildRates(List<Double> rates);

    CourseBuilder buildLanguages(List<Language> languages);

    CourseBuilder buildSubtitles(List<Language> subtitles);

    CourseBuilder buildCategories(List<Category> categories);

    CourseBuilder buildComments(List<Comment> comments);

    CourseBuilder buildLastUpdate(Long lastUpdate);

    CourseBuilder buildPrice(Double price);

    CourseBuilder buildCourseDescription(CourseDescription description);

    Course getResult();

    void restore();

}
