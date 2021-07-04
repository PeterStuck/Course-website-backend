package peterstuck.coursewebsitebackend.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/courses")
@Api(value = "Courses", tags = { "Courses" })
public class CourseResource {

    @Autowired
    @Qualifier(value = "courseServiceImpl")
    private CourseService service;

    private final String FILTER_NAME = "CourseFilter";

    @GetMapping
    @ApiOperation(value = "returns all courses", notes = "When keyword param is provided it will also filter courses with keyword in title.")
    public MappingJacksonValue getAllCourses(
            @ApiParam(value = "additionally searches courses by titles containing keyword when provided")
            @RequestParam(required = false) String keyword) {
        List<Course> courses = service.findAll();
        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" });
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "returns course with given ID", notes = "When course is not found then returns status 404.")
    public MappingJacksonValue getCourseById(@PathVariable int id) throws CourseNotFoundException {
        Course course = service.findById(id);

        return JsonFilter.filterFields(course, FILTER_NAME, null);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation(value = "returns courses with given category ID", notes = "When keyword param is provided it will also filter courses with keyword in title.")
    public MappingJacksonValue getCoursesByCategory(
            @PathVariable int categoryId,
            @ApiParam(value = "additionally searches courses by titles containing keyword when provided")
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
        return course.getTitle().toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean checkCourseHasCategoryWithId(Course course, int categoryId) {
        return course.getCategories()
                .stream()
                .anyMatch(category -> category.getId() == categoryId);
    }


    @PostMapping
    @ApiOperation(value = "adds new course", notes = "Adds new course only when course object is valid.")
    public ResponseEntity<Object> addCourse(
            @ApiParam(value = "new course object, should provide basic information about itself and category/ies", required = true)
            @RequestBody Course course) {
        service.save(course);

        return new ResponseEntity<>(JsonFilter.filterFields(course, FILTER_NAME, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "updates existing course with given ID",
            notes = """
                    Update is being proceed only if course with given ID already exists and there is no error with course data.
                    Returns status 404 when course not found and status 400 when there is problem with updated course data.
                    Endpoint available only for course author and page admin.""")
    public MappingJacksonValue updateCourse(
            @PathVariable int id,
            @ApiParam(value = "course with updated data", required = true)
            @RequestBody Course updatedCourse
    ) throws CourseNotFoundException {
        service.update(id, updatedCourse);

        return JsonFilter.filterFields(updatedCourse, FILTER_NAME, null);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "deletes course with given ID",
            notes = """
                    Deletes course only when course with given ID exists.
                    Returns status 404 when course not found.
                    Endpoint available only for course author and page admin.""")
    public String deleteCourse(@PathVariable int id) throws CourseNotFoundException {
        service.delete(id);

        return "Course with ID: " + id + " was successfully deleted.";
    }

}
