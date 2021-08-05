package peterstuck.coursewebsitebackend.resources.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Courses")
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

    @Operation(summary = "returns all courses", description = "When keyword param is provided it will also filter courses with keyword in title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found courses",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "204", description = "No courses",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) })
    })
    @GetMapping
    public ResponseEntity<Object> getAllCourses(
            @Parameter(name = "additionally searches courses by titles containing keyword when provided")
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


    @Operation(summary = "returns courses with given category id", description = "When keyword param is provided it will also filter courses with keyword in title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found courses",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "204", description = "No courses",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) })
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Object> getCoursesByCategory(
            @PathVariable int categoryId,
            @Parameter(description = "additionally searches courses by titles containing keyword when provided")
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

    @Operation(summary = "returns course with supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found course",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "404", description = "No course with supplied id",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) })
    })
    @GetMapping("/{id}")
    public EntityModel<Course> getCourseById(@PathVariable Long id) throws CourseNotFoundException, JsonProcessingException {
        Course filteredCourse = (Course) filterCourseData(service.findById(id), new String[0]);

        return EntityModel.of(filteredCourse);
    }

    @Operation(summary = "adds new course", description = "Adds new course only when course object is valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad course data",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) })
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Course> addCourse(
            @Parameter(required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "new course object should provide basic information about itself and category/ies", required = true)
            @Valid @RequestBody Course course) throws JsonProcessingException, UserNotExistsException, CourseNotFoundException {
        final String[] courseExceptFields = new String[] { "courseFeedback" };

        Course savedCourse = service.save(course, authHeader);
        Course filteredCourse = (Course) filterCourseData(savedCourse, courseExceptFields);

        return getCourseEntityModel(filteredCourse.getId(), filteredCourse);
    }

    @Operation(summary = "updates existing course with given id",
            description = "Endpoint available only for course author and page admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad course data",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "404", description = "No course with supplied id",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
    })
    @PutMapping("/{id}")
    public EntityModel<Course> updateCourse(
            @Parameter(required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(required = true)
            @PathVariable Long id,
            @Parameter(description = "course with updated data", required = true)
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

    @Operation(summary = "deletes course with supplied id",
            description = "Endpoint available only for course author and page admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course deleted",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "400", description = "Not allowed to delete course",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) }),
            @ApiResponse(responseCode = "404", description = "Course with supplied id not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class)) })
    })
    @DeleteMapping("/{id}")
    public String deleteCourse(
            @Parameter(required = true)
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) throws CourseNotFoundException, NotAnAuthorException {
        service.delete(id, authHeader);

        return "Course with ID: " + id + " was successfully deleted.";
    }
}
