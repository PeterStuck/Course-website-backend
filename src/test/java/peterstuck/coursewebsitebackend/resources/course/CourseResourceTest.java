package peterstuck.coursewebsitebackend.resources.course;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import peterstuck.coursewebsitebackend.factory.course.CourseFactory;
import peterstuck.coursewebsitebackend.factory.course_description.CourseDescriptionFactory;
import peterstuck.coursewebsitebackend.models.Category;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.models.CourseDescription;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CourseRepository repository;

    private List<Course> testCourses;
    private List<Category> testCategories;
    private Course testCourse;
    private CourseDescription testCourseDescription = CourseDescriptionFactory.createCourseDescription(
            0.1,
            "short",
            "long"
    );

    private static final String BASE_PATH = "/api/courses";

    @BeforeEach
    void setUp() {
        initializeTestCategories();

        initializeTestCourseList();

        initializeTestCourse();
    }

    private void initializeTestCategories() {
        testCategories = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var cat = new Category("CATEGORY " + i, 0);
            cat.setId(i + 1);
            testCategories.add(cat);
        }
    }

    private void initializeTestCourseList() {
        testCourses = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var course = CourseFactory.createCourse(
                "TEST " + i,
                Math.max(i, 1.0),
                testCourseDescription,
                testCategories);
            testCourses.add(course);
        }
    }

    private void initializeTestCourse() {
        testCourse = CourseFactory.createCourse(
                "VALID TEST TITLE",
                10.0,
                testCourseDescription,
                testCategories
        );
    }

    @Test
    void givenCoursesWhenGetCoursesThenStatus200AndListOfExistingCourses() throws Exception {
        when(repository.findAll()).thenReturn(testCourses);

        List<Course> courses = makeRequestToGetCourses(BASE_PATH);

        verify(repository).findAll();

        assertThat(courses, hasSize(3));
        assertThat(courses.get(0).getTitle(), equalTo("TEST 0"));
        assertThat(courses.get(0).getPrice(), equalTo(1.0));
    }

    @Test
    void whenNoCoursesPresentReturnEmptyListAndStatus200() throws Exception {
        List<Course> courses = makeRequestToGetCourses(BASE_PATH);

        assertThat(courses, hasSize(0));
    }

    @Test
    void whenKeywordIsPassedShouldReturnFilteredCourses() throws Exception {
        testCourses.add(CourseFactory.createCourse("TEST WITH KEYWORD", 5.0, testCourseDescription));
        testCourses.add(CourseFactory.createCourse("TEST WITH KEYWORD 2", 4.5, testCourseDescription));
        when(repository.findAll()).thenReturn(testCourses);

        String keyword = "KEYWORD";
        List<Course> filteredCourses = makeRequestToGetCourses(BASE_PATH + "?keyword=" + keyword);

        assertThat(filteredCourses, hasSize(2));
        assertThat(filteredCourses.get(0).getTitle(), equalTo("TEST WITH KEYWORD"));
        assertThat(filteredCourses.get(0).getPrice(), equalTo(5.0));
    }

    @Test
    void whenCourseWithGivenIdExistsThenReturnCourse() throws Exception {
        long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.ofNullable(testCourse));

        var response = makeRequestToGetSingleCourse(BASE_PATH + "/" + id, status().isOk());
        Course course = objectMapper().readValue(response.getContentAsString(), Course.class);

        verify(repository).findById(id);
        assertThat(course.getTitle(), equalTo(testCourse.getTitle()));
        assertThat(course.getPrice(), equalTo(testCourse.getPrice()));
    }

    @Test
    void whenCourseWithGivenIdNotExistsThenStatus404() throws Exception {
        var response = makeRequestToGetSingleCourse(BASE_PATH + "/999", status().isNotFound());

        assertThat(response.getContentAsString(), containsString("message"));
        assertThat(response.getContentAsString(), containsString("not found"));
    }

    @Test
    void whenNoCoursesWithCategoryReturnEmptyListWithStatus200() throws Exception {
        when(repository.findAll()).thenReturn(testCourses);
        List<Course> courses = makeRequestToGetCourses(BASE_PATH + "/category/999");

        verify(repository).findAll();
        assertThat(courses, hasSize(0));
    }

    @Test
    void shouldReturnFilteredListOfCoursesWithGivenCategoryIdAndStatus200() throws Exception {
        testCourses.get(0).setCategories(Collections.emptyList());
        when(repository.findAll()).thenReturn(testCourses);

        List<Course> courses = makeRequestToGetCourses(BASE_PATH + "/category/1");

        assertThat(courses, hasSize(2));
        assertThat(courses.get(0).getTitle(), not(equalTo(testCourses.get(0).getTitle())));
    }

    @Test
    void shouldReturnListOfCoursesFilteredByCategoryAndKeywordWhenKeywordProvided() throws Exception {
        testCourses.add(CourseFactory.createCourse("Course with keyword 1", 1.0, testCourseDescription, testCategories));
        testCourses.add(CourseFactory.createCourse("Course with keyword 2", 2.0, testCourseDescription, testCategories));
        when(repository.findAll()).thenReturn(testCourses);

        String keyword = "KeYwoRD";
        List<Course> courses = makeRequestToGetCourses(BASE_PATH + "/category/1?keyword=" + keyword);

        assertThat(courses, hasSize(2));
        assertThat(courses.get(0).getTitle(), equalTo("Course with keyword 1"));
    }

    @Test
    void shouldAddNewCourseAndReturnNewObjectWithStatus201() throws Exception {
        when(repository.findAll()).thenReturn(testCourses);
        when(repository.save(any())).then(invocationOnMock -> {
            testCourses.add(testCourse);
            return testCourse;
        });

        var response = makePostCourseRequest(testCourse, status().isCreated());
        Course course = objectMapper().readValue(response.getContentAsString(), Course.class);

        verify(repository).save(any());
        assertThat(course.getTitle(), equalTo(testCourse.getTitle()));
        assertThat(repository.findAll(), hasSize(4));
    }

    @Test
    void shouldDeleteCourseWhenExists() throws Exception {
        testCourses.add(testCourse);
        when(repository.findAll()).thenReturn(testCourses);
        when(repository.findById(1L)).then(invocationOnMock -> {
                testCourses.remove(testCourse);
                return Optional.ofNullable(testCourse);
        });

        mvc.perform(delete(BASE_PATH + "/1"))
                .andExpect(status().isOk());

        verify(repository).delete(testCourse);
        assertThat(repository.findAll(), hasSize(3));
    }

    @Test
    void shouldReturnStatus404WhenCourseNotFound() throws Exception {
        mvc.perform(delete(BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateWhenCourseDataIsValid() throws Exception {
        long id = 1L;
        testCourse.setId(id);
        when(repository.findById(id)).thenReturn(Optional.ofNullable(testCourse));
        var updatedTestCourse = cloneCourse(testCourse);
        updatedTestCourse.setTitle("NEW TITLE");

        makePutCourseRequest(BASE_PATH + "/1", updatedTestCourse, status().isOk());

        verify(repository).findById(id);
        verify(repository).save(testCourse);
        assertThat(repository.findById(id).get().getTitle(), equalTo("NEW TITLE"));
    }

    @Test
    void shouldReturnErrorMessagesWhenCourseDataIsInvalid() throws Exception {
        var invalidCourse = CourseFactory.createCourse(null, null, null);
        String response = makePostCourseRequest(invalidCourse, status().isBadRequest()).getContentAsString();

        // check name of error fields exist in response
        assertThat(response, containsString("title"));
        assertThat(response, containsString("price"));
        assertThat(response, containsString("courseDescription"));
        // check that correct error messages are returning with response
        assertThat(response, containsString("Title is mandatory."));
        assertThat(response, containsString("Price is mandatory."));
        assertThat(response, containsString("Course must have a description."));
    }

    @Test
    void shouldReturnErrorMessagesWhenCourseDescriptionDataIsInvalid() throws Exception {
        var invalidCourseDescription = CourseDescriptionFactory.createCourseDescription(
                null,
                null,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus quis vestibulum eros, at porta lorem. Cras tincidunt laoreet diam, vitae placerat metus laoreet sit amet. Nulla eros dui, molestie ac pharetra ut, mattis vitae ex. Etiam eget convallis mauris. Morbi blandit tortor vitae quam mollis, sit amet pellentesque nulla ultrices. Vivamus blandit quam porta, blandit urna vitae, sagittis nisi. Quisque placerat efficitur metus, non fringilla nisi semper sit amet. Vivamus eget tellus in justo ele."
        );
        var invalidCourse = CourseFactory.createCourse("TEST", 0.0, invalidCourseDescription);
        String response = makePostCourseRequest(invalidCourse, status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("Duration is mandatory."));
        assertThat(response, containsString("Short description is mandatory."));
        assertThat(response, containsString("Long description length should not be greater than 500 characters."));
        assertThat(response, containsString("Title should have between 5 and 50 characters."));
    }

    private List<Course> makeRequestToGetCourses(String path) throws Exception {
        MockHttpServletResponse response =  mvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        return objectMapper().readValue(response.getContentAsString(), new TypeReference<>(){});
    }

    private MockHttpServletResponse makeRequestToGetSingleCourse(String path, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    private void makePutCourseRequest(String path, Course content, ResultMatcher expectedStatus) throws Exception {
        mvc.perform(
                put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonFilter.castObjectToJsonString(content, "CourseFilter", null))
                    )
                .andExpect(expectedStatus);
    }

    private MockHttpServletResponse makePostCourseRequest(Course content, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(
                post(CourseResourceTest.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonFilter.castObjectToJsonString(content, "CourseFilter", null))
                )
                .andExpect(expectedStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    private Course cloneCourse(Course original) {
        Course cloned = new Course();
        cloned.setId(original.getId());
        cloned.setTitle(original.getTitle());
        cloned.setLastUpdate(original.getLastUpdate());
        cloned.setComments(original.getComments());
        cloned.setCourseDescription(original.getCourseDescription());
        cloned.setSubtitles(original.getSubtitles());
        cloned.setCategories(original.getCategories());
        cloned.setPrice(original.getPrice());
        cloned.setLanguages(original.getLanguages());
        cloned.setRates(original.getRates());
        return cloned;
    }

}