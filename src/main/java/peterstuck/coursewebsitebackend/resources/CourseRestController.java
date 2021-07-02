package peterstuck.coursewebsitebackend.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;

@RestController
@RequestMapping("/courses")
public class CourseRestController {

    @Autowired
    private CourseRepository repository;

    @GetMapping
    public Course getAllCourses() {
        return null;
    }

}
