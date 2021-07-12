package peterstuck.coursewebsitebackend.services.course;

import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.exceptions.NotAnAuthorException;
import peterstuck.coursewebsitebackend.models.course.Course;

import java.util.List;

public interface CourseService {

    List<Course> findAll();

    Course findById(Long id) throws CourseNotFoundException;

    Course save(Course course);

    Course update(Long id, String token, Course updated) throws CourseNotFoundException, NotAnAuthorException;

    void delete(Long id, String token) throws CourseNotFoundException, NotAnAuthorException;

}
