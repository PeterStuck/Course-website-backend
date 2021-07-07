package peterstuck.coursewebsitebackend.resources.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.services.course.CourseService;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import javax.validation.Valid;
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
    @ApiOperation(value = "returns all courses", notes = """
        When keyword param is provided it will also filter courses with keyword in title.
        Return status 200 when courses where successfully returned, 204 when no courses are available.
        """)
    public ResponseEntity<Object> getAllCourses(
            @ApiParam(value = "additionally searches courses by titles containing keyword when provided")
            @RequestParam(required = false) String keyword) throws JsonProcessingException {
        List<Course> courses = service.findAll();
        if (keyword != null) {
            courses = courses.stream()
                    .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                    .collect(Collectors.toList());
        }

        return getResponseAndStatus((List<Course>) JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" }));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "returns course with given ID", notes = "When course is not found then returns status 404.")
    public Course getCourseById(@PathVariable Long id) throws CourseNotFoundException, JsonProcessingException {
        Course course = service.findById(id);

        return (Course) JsonFilter.filterFields(course, FILTER_NAME, null);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation(value = "returns courses with given category ID", notes = """
        When keyword param is provided it will also filter courses with keyword in title.
        Return status 200 when courses where successfully returned, 204 when no courses are available.
        """)
    public ResponseEntity<Object> getCoursesByCategory(
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

        return getResponseAndStatus((List<Course>) JsonFilter.filterFields(courses, FILTER_NAME, new String[] { "courseDescription" }));
    }

    private boolean checkCourseTitleContainsKeyword(Course course, String keyword) {
        return course.getTitle().toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean checkCourseHasCategoryWithId(Course course, int categoryId) {
        return course.getCategories()
                .stream()
                .anyMatch(category -> category.getId() == categoryId);
    }

    private ResponseEntity<Object> getResponseAndStatus(List<Course> courses) {
        return new ResponseEntity<>(
                courses,
                (courses.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "adds new course", notes = "Adds new course only when course object is valid.")
    public Course addCourse(
            @ApiParam(value = "new course object, should provide basic information about itself and category/ies", required = true)
            @Valid @RequestBody Course course) throws JsonProcessingException {
        service.save(course);

        return (Course) JsonFilter.filterFields(course, FILTER_NAME, null);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "updates existing course with given ID",
            notes = """
                    Update is being proceed only if course with given ID already exists and there is no error with course data.
                    Returns status 404 when course not found and status 400 when there is problem with updated course data.
                    Endpoint available only for course author and page admin.""")
    public Course updateCourse(
            @PathVariable Long id,
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
    public String deleteCourse(@PathVariable Long id) throws CourseNotFoundException {
        service.delete(id);

        return "Course with ID: " + id + " was successfully deleted.";
    }
}
