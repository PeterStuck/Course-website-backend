package peterstuck.coursewebsitebackend.services.course;

import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.course.Course;

import java.util.List;

public interface CourseService {

    List<Course> findAll();

    Course findById(Long id) throws CourseNotFoundException;

    Course save(Course course);

    Course update(Long id, Course updated) throws CourseNotFoundException;

    void delete(Long id) throws CourseNotFoundException;

}
