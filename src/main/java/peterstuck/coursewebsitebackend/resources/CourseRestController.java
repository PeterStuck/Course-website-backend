package peterstuck.coursewebsitebackend.resources;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.models.CourseDescription;
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
    public MappingJacksonValue getAllCourses(@RequestParam(required = false) String keyword) {
        List<Course> courses = repository.findAll();
        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return filterJson(courses, new String[] { "courseDescription" });
    }

    @GetMapping("/{id}")
    public MappingJacksonValue getCourseById(@PathVariable int id) throws CourseNotFoundException {
        Course course = getCourseOrThrowCourseNotFound(id);

        return filterJson(course, null);
    }

    // TODO Check if works after add test data
    @GetMapping("/category/{categoryId}")
    public MappingJacksonValue getCoursesByCategory(
            @PathVariable int categoryId,
            @RequestParam(required = false) String keyword) {
        List<Course> courses = repository.findAll()
                .stream()
                .filter(course -> checkCourseHasCategoryWithId(course, categoryId))
                .collect(Collectors.toList());

        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return filterJson(courses, new String[] { "courseDescription" });
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
        course.setCourseDescription(new CourseDescription(15.5, "short", "looooooong"));

        repository.save(course);

        return filterJson(course, null);
    }

    @PutMapping("/{id}")
    public MappingJacksonValue updateCourse(@PathVariable int id, @RequestBody Course updatedCourse) throws CourseNotFoundException {
        // check if course with given id exists
        getCourseOrThrowCourseNotFound(id);

        if (id != updatedCourse.getId()) {
            throw new IllegalArgumentException("Path ID and request body ID must match!");
        }

        repository.save(updatedCourse);

        return filterJson(updatedCourse, null);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable int id) throws CourseNotFoundException {
        Course course = getCourseOrThrowCourseNotFound(id);
        repository.delete(course);

        return "Course with ID: " + id + " was successfully deleted.";
    }

    private MappingJacksonValue filterJson(Object obj, String[] exceptFields) {
        SimpleBeanPropertyFilter simpleBeanPropertyFilter =
                SimpleBeanPropertyFilter.serializeAllExcept(
                        (exceptFields != null ? String.join(" ", exceptFields) : "")
                );

        String courseFilterName = "CourseFilter";
        FilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter(courseFilterName, simpleBeanPropertyFilter);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(obj);
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    private Course getCourseOrThrowCourseNotFound(int id) throws CourseNotFoundException {
        Optional<Course> course = repository.findById(id);
        if (course.isEmpty())
            throw new CourseNotFoundException("Course with id: " + id + " not found!");
        return course.get();
    }

}
