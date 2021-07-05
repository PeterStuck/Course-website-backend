package peterstuck.coursewebsitebackend.builder.course;

import peterstuck.coursewebsitebackend.models.*;

import java.util.List;
import java.util.Set;

public interface CourseBuilder {

    CourseBuilder buildTitle(String title);

    CourseBuilder buildRates(List<Double> rates);

    CourseBuilder buildLanguages(Set<Language> languages);

    CourseBuilder buildSubtitles(Set<Language> subtitles);

    CourseBuilder buildCategories(List<Category> categories);

    CourseBuilder buildComments(List<Comment> comments);

    CourseBuilder buildLastUpdate(Long lastUpdate);

    CourseBuilder buildPrice(Double price);

    CourseBuilder buildCourseDescription(CourseDescription description);

    Course getResult();

    void restore();

}
