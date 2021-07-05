package peterstuck.coursewebsitebackend.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.services.CourseService;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<Course> getAllCourses(
            @ApiParam(value = "additionally searches courses by titles containing keyword when provided")
            @RequestParam(required = false) String keyword) throws JsonProcessingException {
        List<Course> courses = service.findAll();
        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return (List<Course>) JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" });
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "returns course with given ID", notes = "When course is not found then returns status 404.")
    public Course getCourseById(@PathVariable int id) throws CourseNotFoundException, JsonProcessingException {
        Course course = service.findById(id);

        return (Course) JsonFilter.filterFields(course, FILTER_NAME, null);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation(value = "returns courses with given category ID", notes = "When keyword param is provided it will also filter courses with keyword in title.")
    public List<Course> getCoursesByCategory(
            @PathVariable int categoryId,
            @ApiParam(value = "additionally searches courses by titles containing keyword when provided")
            @RequestParam(required = false) String keyword
    ) throws JsonProcessingException {
        List<Course> courses = service.findAll()
                .stream()
                .filter(course -> checkCourseHasCategoryWithId(course, categoryId))
                .collect(Collectors.toList());

        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return (List<Course>) JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" });
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
            @Valid @RequestBody Course course) throws JsonProcessingException {
        service.save(course);

        return new ResponseEntity<>(JsonFilter.filterFields(course, FILTER_NAME, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "updates existing course with given ID",
            notes = """
                    Update is being proceed only if course with given ID already exists and there is no error with course data.
                    Returns status 404 when course not found and status 400 when there is problem with updated course data.
                    Endpoint available only for course author and page admin.""")
    public Course updateCourse(
            @PathVariable int id,
            @ApiParam(value = "course with updated data", required = true)
            @Valid @RequestBody Course updatedCourse
    ) throws CourseNotFoundException, JsonProcessingException {
        service.update(id, updatedCourse);

        return (Course) JsonFilter.filterFields(updatedCourse, FILTER_NAME, null);
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

    // Not working in advisor
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
