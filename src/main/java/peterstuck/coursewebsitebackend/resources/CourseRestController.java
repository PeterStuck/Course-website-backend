package peterstuck.coursewebsitebackend.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseRestController {

    @Autowired
    private CourseRepository repository;

    @GetMapping
    public List<Course> getAllCourses(@RequestParam(required = false) String keyword) {
        if (keyword != null) {
            return repository.findAll()
                    .stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable int id) throws CourseNotFoundException {
        Optional<Course> course = repository.findById(id);
        if (course.isEmpty()) {
            throw new CourseNotFoundException("Course with id: " + id + " not found!");
        }

        return course.get();
    }

    @GetMapping("/category/{categoryId}")
    public List<Course> getCoursesByCategory(
            @PathVariable int categoryId,
            @RequestParam(required = false) String keyword) {
        List<Course> courses = repository.findAll()
                .stream()
                .filter(course -> checkCourseHasCategoryWithId(course, categoryId))
                .collect(Collectors.toList());

        if (keyword != null) {
            return courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return courses;
    }

    private boolean checkCourseTitleContainsKeyword(Course course, String keyword) {
        return course.getTitle().contains(keyword);
    }

    private boolean checkCourseHasCategoryWithId(Course course, int categoryId) {
        return course.getCategories()
                .stream()
                .anyMatch(category -> category.getId() == categoryId);
    }

}
