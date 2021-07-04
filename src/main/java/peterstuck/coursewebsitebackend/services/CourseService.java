package peterstuck.coursewebsitebackend.services;

import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;

import java.util.List;

public interface CourseService {

    List<Course> findAll();

    Course findById(int id) throws CourseNotFoundException;

    Course save(Course course);

    Course update(int id, Course updated) throws CourseNotFoundException;

    void delete(int id) throws CourseNotFoundException;

}
