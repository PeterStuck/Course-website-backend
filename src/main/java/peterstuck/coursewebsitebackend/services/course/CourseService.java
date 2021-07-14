package peterstuck.coursewebsitebackend.services.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.exceptions.NotAnAuthorException;
import peterstuck.coursewebsitebackend.exceptions.UserNotExistsException;
import peterstuck.coursewebsitebackend.models.course.Course;

import java.util.List;

public interface CourseService {

    /**
     * @param keyword optional additional filter by title
     */
    List<Course> findAll(String keyword) throws JsonProcessingException;

    List<Course> findAllByCategory(String keyword, int categoryId) throws JsonProcessingException;

    Course findById(Long id) throws CourseNotFoundException;

    Course save(Course course, String token) throws UserNotExistsException;

    Course update(Long id, String token, Course updated) throws CourseNotFoundException, NotAnAuthorException;

    void delete(Long id, String token) throws CourseNotFoundException, NotAnAuthorException;

}
