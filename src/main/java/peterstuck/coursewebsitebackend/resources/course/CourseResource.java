package peterstuck.coursewebsitebackend.resources.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.exceptions.NotAnAuthorException;
import peterstuck.coursewebsitebackend.exceptions.UserNotExistsException;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.services.course.CourseService;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/courses")
@Api(value = "Courses", tags = { "Courses" })
public class CourseResource {

    @Autowired
    @Qualifier(value = "courseServiceImpl")
    private CourseService service;

    private final String FILTER_NAME = "JsonFilter";

    private final String[] USER_EXCEPT_FIELDS = new String[] {
            "password",
            "roles",
            "userActivity",
            "userDetail",
            "purchasedCourses"
    };

    @GetMapping
    @ApiOperation(value = "returns all courses", notes = """
        When keyword param is provided it will also filter courses with keyword in title.
        Return status 200 when courses where successfully returned, 204 when no courses are available.
        """)
    public ResponseEntity<Object> getAllCourses(
            @ApiParam(value = "additionally searches courses by titles containing keyword when provided")
            @RequestParam(required = false) String keyword) throws JsonProcessingException {
        List<Course> courses = service.findAll(keyword);

        String[] courseExceptFields = new String[] {
                "duration",
                "longDescription",
                "mainTopics",
                "requirements",
                "comments"
        };

        return getResponseAndStatus((List<Course>) filterCourseData(courses, courseExceptFields));
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
        List<Course> courses = service.findAllByCategory(keyword, categoryId);

        String[] courseExceptFields = new String[] {
                "duration",
                "longDescription",
                "mainTopics",
                "requirements",
                "comments"
        };

        return getResponseAndStatus((List<Course>) filterCourseData(courses, courseExceptFields));
    }

    private ResponseEntity<Object> getResponseAndStatus(List<Course> courses) {
        return new ResponseEntity<>(
                courses,
                (courses.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
        );
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "returns course with given ID", notes = "When course is not found then returns status 404.")
    public EntityModel<Course> getCourseById(@PathVariable Long id) throws CourseNotFoundException, JsonProcessingException {
        Course filteredCourse = (Course) filterCourseData(service.findById(id), new String[0]);

        return EntityModel.of(filteredCourse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "adds new course", notes = "Adds new course only when course object is valid.")
    public EntityModel<Course> addCourse(
            @ApiParam(required = true)
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "new course object should provide basic information about itself and category/ies", required = true)
            @Valid @RequestBody Course course) throws JsonProcessingException, UserNotExistsException, CourseNotFoundException {
        final String[] courseExceptFields = new String[] { "courseFeedback" };

        Course savedCourse = service.save(course, authHeader);
        Course filteredCourse = (Course) filterCourseData(savedCourse, courseExceptFields);

        return getCourseEntityModel(filteredCourse.getId(), filteredCourse);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "updates existing course with given ID",
            notes = """
                    Update is being proceed only if course with given ID already exists and there is no error with course data.
                    Returns status 404 when course not found and status 400 when there is problem with updated course data.
                    Endpoint available only for course author and page admin.""")
    public EntityModel<Course> updateCourse(
            @ApiParam(required = true)
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(required = true)
            @PathVariable Long id,
            @ApiParam(value = "course with updated data", required = true)
            @Valid @RequestBody Course updatedCourse
    ) throws CourseNotFoundException, JsonProcessingException, NotAnAuthorException {
        String[] courseExceptFields = new String[] { "courseFeedback" };

        Course updated = service.update(id, authHeader, updatedCourse);
        Course filteredCourse = (Course) filterCourseData(updated, courseExceptFields);

        return getCourseEntityModel(id, filteredCourse);
    }

    private Object filterCourseData(Object rawObject, String[] courseExceptFields) throws JsonProcessingException {
        String[] exceptFields = Stream.concat(Arrays.stream(courseExceptFields), Arrays.stream(USER_EXCEPT_FIELDS)).toArray(String[]::new);

        return JsonFilter.filterFields(rawObject, FILTER_NAME, exceptFields);
    }

    private EntityModel<Course> getCourseEntityModel(Long id, Course course) throws CourseNotFoundException, JsonProcessingException {
        EntityModel<Course> model = EntityModel.of(course);
        WebMvcLinkBuilder linkToCourse = linkTo(methodOn(this.getClass()).getCourseById(id));
        model.add(linkToCourse.withRel("course-link"));
        return model;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "deletes course with given ID",
            notes = """
                    Deletes course only when course with given ID exists.
                    Returns status 404 when course not found.
                    Endpoint available only for course author and page admin.""")
    public String deleteCourse(
            @ApiParam(required = true)
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) throws CourseNotFoundException, NotAnAuthorException {
        service.delete(id, authHeader);

        return "Course with ID: " + id + " was successfully deleted.";
    }
}
