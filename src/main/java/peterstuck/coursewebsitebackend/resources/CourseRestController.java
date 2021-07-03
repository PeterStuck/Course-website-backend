package peterstuck.coursewebsitebackend.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CourseInvalidDataException;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.services.CourseService;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseRestController {

    @Autowired
    @Qualifier(value = "courseServiceImpl")
    private CourseService service;

    private final String FILTER_NAME = "CourseFilter";

    @GetMapping
    public MappingJacksonValue getAllCourses(@RequestParam(required = false) String keyword) {
        List<Course> courses = service.findAll();
        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" });
    }

    @GetMapping("/{id}")
    public MappingJacksonValue getCourseById(@PathVariable int id) throws CourseNotFoundException {
        Course course = service.findById(id);

        return JsonFilter.filterFields(course, FILTER_NAME, null);
    }

    // TODO Check if works after add test data
    @GetMapping("/category/{categoryId}")
    public MappingJacksonValue getCoursesByCategory(
            @PathVariable int categoryId,
            @RequestParam(required = false) String keyword
    ) {
        List<Course> courses = service.findAll()
                .stream()
                .filter(course -> checkCourseHasCategoryWithId(course, categoryId))
                .collect(Collectors.toList());

        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" });
    }

    private boolean checkCourseTitleContainsKeyword(Course course, String keyword) {
        return course.getTitle().contains(keyword);
    }

    private boolean checkCourseHasCategoryWithId(Course course, int categoryId) {
        return course.getCategories()
                .stream()
                .anyMatch(category -> category.getId() == categoryId);
    }


    @PostMapping
    public MappingJacksonValue addCourse(@RequestBody Course course) {
        service.save(course);

        return JsonFilter.filterFields(course, FILTER_NAME, null);
    }

    @PutMapping("/{id}")
    public MappingJacksonValue updateCourse(
            @PathVariable int id,
            @RequestBody Course updatedCourse
    ) throws CourseNotFoundException, CourseInvalidDataException {
        service.update(id, updatedCourse);

        return JsonFilter.filterFields(updatedCourse, FILTER_NAME, null);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable int id) throws CourseNotFoundException {
        service.delete(id);

        return "Course with ID: " + id + " was successfully deleted.";
    }

}
