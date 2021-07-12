package peterstuck.coursewebsitebackend.resources.course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.factory.course.CourseFactory;
import peterstuck.coursewebsitebackend.factory.course_description.CourseDescriptionFactory;
import peterstuck.coursewebsitebackend.models.course.Category;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.models.course.CourseDescription;
import peterstuck.coursewebsitebackend.models.course.CourseFeedback;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserActivity;
import peterstuck.coursewebsitebackend.models.user.UserDetail;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.resources.TestRequestUtils;
import peterstuck.coursewebsitebackend.utils.JwtUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CourseResourceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private List<Course> testCourses;
    private List<Category> testCategories;
    private Course testCourse;
    private CourseDescription testCourseDescription = CourseDescriptionFactory.createCourseDescription(
            0.1,
            "short",
            "long"
    );

    private User testUser;

    private TestRequestUtils tru;

    private static final String BASE_PATH = "/api/courses";

    @BeforeEach
    void setUp() {
        tru = new TestRequestUtils(Course.class, mvc, "CourseFilter");

        initializeTestCategories();

        initializeTestCourseList();

        initializeTestCourse();

        initializeTestUser();
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
            course.setCourseFeedback(new CourseFeedback());
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
        testCourse.setCourseFeedback(new CourseFeedback());
    }

    private void initializeTestUser() {
        testUser = new User();
        testUser.setEmail("email@email.com");
        testUser.setFirstName("Name");
        testUser.setLastName("Last");
        testUser.setPassword(encoder.encode("user"));
        testUser.setUserActivity(new UserActivity());
        testUser.setRoles(Collections.singletonList(new Role("ROLE_USER")));
        testUser.setUserDetail(new UserDetail());
    }

    @Test
    void givenCoursesWhenGetCoursesThenStatus200AndListOfExistingCourses() throws Exception {
        when(courseRepository.findAll()).thenReturn(testCourses);

        List<Course> courses = (List<Course>) tru.makeRequestToGetItems(BASE_PATH, status().isOk());

        verify(courseRepository).findAll();

        assertThat(courses, hasSize(3));
        assertThat(courses.get(0).getTitle(), equalTo("TEST 0"));
        assertThat(courses.get(0).getPrice(), equalTo(1.0));
    }

    @Test
    void whenNoCoursesPresentReturnEmptyListAndStatus204() throws Exception {
        assertThat(tru.makeRequestToGetItems(BASE_PATH, status().isNoContent()), hasSize(0));
    }

    @Test
    void whenKeywordIsPassedShouldReturnFilteredCourses() throws Exception {
        var course = CourseFactory.createCourse("TEST WITH KEYWORD", 5.0, testCourseDescription);
        course.setCourseFeedback(new CourseFeedback());
        var course2 = CourseFactory.createCourse("TEST WITH KEYWORD 2", 4.5, testCourseDescription);
        course2.setCourseFeedback(new CourseFeedback());
        testCourses.add(course);
        testCourses.add(course2);

        when(courseRepository.findAll()).thenReturn(testCourses);

        String keyword = "KEYWORD";
        List<Course> filteredCourses = (List<Course>) tru.makeRequestToGetItems(BASE_PATH + "?keyword=" + keyword, status().isOk());

        assertThat(filteredCourses, hasSize(2));
        assertThat(filteredCourses.get(0).getTitle(), equalTo("TEST WITH KEYWORD"));
        assertThat(filteredCourses.get(0).getPrice(), equalTo(5.0));
    }

    @Test
    void whenCourseWithGivenIdExistsThenReturnCourse() throws Exception {
        long id = 1L;
        when(courseRepository.findById(id)).thenReturn(Optional.ofNullable(testCourse));

        var response = tru.makeRequestToGetSingleItem(BASE_PATH + "/" + id, status().isOk());
        Course course = TestRequestUtils.mapper.readValue(response.getContentAsString(), Course.class);

        verify(courseRepository).findById(id);
        assertThat(course.getTitle(), equalTo(testCourse.getTitle()));
        assertThat(course.getPrice(), equalTo(testCourse.getPrice()));
    }

    @Test
    void whenCourseWithGivenIdNotExistsThenStatus404() throws Exception {
        var response = tru.makeRequestToGetSingleItem(BASE_PATH + "/999", status().isNotFound());

        assertThat(response.getContentAsString(), containsString("message"));
        assertThat(response.getContentAsString(), containsString("not found"));
    }

    @Test
    void whenNoCoursesWithCategoryReturnEmptyListWithStatus204() throws Exception {
        when(courseRepository.findAll()).thenReturn(testCourses);
        List<Course> courses = (List<Course>) tru.makeRequestToGetItems(BASE_PATH + "/category/999", status().isNoContent());

        verify(courseRepository).findAll();
        assertThat(courses, hasSize(0));
    }

    @Test
    void shouldReturnFilteredListOfCoursesWithGivenCategoryIdAndStatus200() throws Exception {
        testCourses.get(0).setCategories(Collections.emptyList());
        when(courseRepository.findAll()).thenReturn(testCourses);

        List<Course> courses = (List<Course>) tru.makeRequestToGetItems(BASE_PATH + "/category/1", status().isOk());

        assertThat(courses, hasSize(2));
        assertThat(courses.get(0).getTitle(), not(equalTo(testCourses.get(0).getTitle())));
    }

    @Test
    void shouldReturnListOfCoursesFilteredByCategoryAndKeywordWhenKeywordProvided() throws Exception {
        var course = CourseFactory.createCourse("Course with keyword 1", 1.0, testCourseDescription, testCategories);
        course.setCourseFeedback(new CourseFeedback());
        var course2 = CourseFactory.createCourse("Course with keyword 2", 2.0, testCourseDescription, testCategories);
        course2.setCourseFeedback(new CourseFeedback());
        testCourses.add(course);
        testCourses.add(course2);
        when(courseRepository.findAll()).thenReturn(testCourses);

        String keyword = "KeYwoRD";
        List<Course> courses = (List<Course>) tru.makeRequestToGetItems(BASE_PATH + "/category/1?keyword=" + keyword, status().isOk());
        verify(courseRepository).findAll();
        assertThat(courses, hasSize(2));
        assertThat(courses.get(0).getTitle(), equalTo("Course with keyword 1"));
    }

    @WithMockUser
    @Test
    void shouldAddNewCourseAndReturnNewObjectWithStatus201() throws Exception {
        when(courseRepository.findAll()).thenReturn(testCourses);
        when(courseRepository.save(any())).then(invocationOnMock -> {
            testCourses.add(testCourse);
            return testCourse;
        });

        var response = tru.makePostRequest(BASE_PATH, testCourse, status().isCreated());
        Course course = TestRequestUtils.mapper.readValue(response.getContentAsString(), Course.class);

        verify(courseRepository).save(any());
        assertThat(course.getTitle(), equalTo(testCourse.getTitle()));
        assertThat(courseRepository.findAll(), hasSize(4));
    }

    @WithMockUser
    @Test
    void shouldDeleteCourseWhenExists() throws Exception {
        testCourse.getAuthors().add(testUser);
        testCourses.add(testCourse);
        when(courseRepository.findAll()).thenReturn(testCourses);
        when(courseRepository.findById(1L)).then(invocationOnMock -> {
                testCourses.remove(testCourse);
                return Optional.ofNullable(testCourse);
        });
        when(userRepository.findByEmail(any())).thenReturn(testUser);
        when(jwtUtil.extractUsername(any())).thenReturn("some@email.com");

        tru.makeDeleteRequest(BASE_PATH + "/1", status().isOk());

        verify(courseRepository).delete(testCourse);
        assertThat(courseRepository.findAll(), hasSize(3));
    }

    @WithMockUser
    @Test
    void whenRequesterAttemptToDeleteCourseAndIsNotAnAuthorThenReturnStatus400AndMessage() throws Exception {
        when(courseRepository.findById(any())).thenReturn(Optional.ofNullable(testCourse));
        when(userRepository.findByEmail(any())).thenReturn(testUser);
        when(jwtUtil.extractUsername(any())).thenReturn(testUser.getEmail());

        String response = tru.makeDeleteRequest(BASE_PATH + "/99", status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("You are allow to update or delete only own courses."));
        assertThat(response, containsString("timestamp"));
    }

    @WithMockUser
    @Test
    void shouldReturnStatus404WhenCourseNotFound() throws Exception {
        tru.makeDeleteRequest(BASE_PATH + "/1", status().isNotFound());
    }

    @WithMockUser
    @Test
    void shouldUpdateWhenCourseDataIsValid() throws Exception {
        long id = 1L;
        testCourse.setId(id);
        testCourse.getAuthors().add(testUser);
        when(courseRepository.findById(id)).thenReturn(Optional.ofNullable(testCourse));
        when(userRepository.findByEmail(any())).thenReturn(testUser);
        when(jwtUtil.extractUsername(any())).thenReturn("some@email.com");

        var updatedTestCourse = cloneCourse(testCourse);
        updatedTestCourse.setTitle("NEW TITLE");
        tru.makePutRequest(BASE_PATH + "/1", updatedTestCourse, status().isOk());

        verify(courseRepository).findById(id);
        verify(courseRepository).save(testCourse);
        assertThat(courseRepository.findById(id).get().getTitle(), equalTo("NEW TITLE"));
    }

    @WithMockUser
    @Test
    void whenRequesterToUpdateCourseIsNotOneOfAuthorsShouldReturnStatus400AndAppropriateMessage() throws Exception {
        when(courseRepository.findById(1L)).thenReturn(Optional.ofNullable(testCourse));
        when(userRepository.findByEmail(any())).thenReturn(testUser);

        String response = tru.makePutRequest(BASE_PATH + "/1", testCourse, status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("You are allow to update or delete only own courses."));
    }

    @WithMockUser
    @Test
    void shouldReturnErrorMessagesWhenCourseDataIsInvalid() throws Exception {
        var invalidCourse = CourseFactory.createCourse(null, null, null, null);
        String response = tru.makePostRequest(BASE_PATH, invalidCourse, status().isBadRequest()).getContentAsString();

        // check name of error fields exist in response
        assertThat(response, containsString("title"));
        assertThat(response, containsString("price"));
        assertThat(response, containsString("courseDescription"));
        // check that correct error messages are returning with response
        assertThat(response, containsString("Title is mandatory."));
        assertThat(response, containsString("Price is mandatory."));
        assertThat(response, containsString("Course must have a description."));
    }

    @WithMockUser
    @Test
    void shouldReturnErrorMessagesWhenCourseDescriptionDataIsInvalid() throws Exception {
        var invalidCourseDescription = CourseDescriptionFactory.createCourseDescription(
                null,
                null,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus quis vestibulum eros, at porta lorem. Cras tincidunt laoreet diam, vitae placerat metus laoreet sit amet. Nulla eros dui, molestie ac pharetra ut, mattis vitae ex. Etiam eget convallis mauris. Morbi blandit tortor vitae quam mollis, sit amet pellentesque nulla ultrices. Vivamus blandit quam porta, blandit urna vitae, sagittis nisi. Quisque placerat efficitur metus, non fringilla nisi semper sit amet. Vivamus eget tellus in justo ele."
        );
        var invalidCourse = CourseFactory.createCourse("TEST", 0.0, invalidCourseDescription);
        String response = tru.makePostRequest(BASE_PATH, invalidCourse, status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("Duration is mandatory."));
        assertThat(response, containsString("Short description is mandatory."));
        assertThat(response, containsString("Long description length should not be greater than 500 characters."));
        assertThat(response, containsString("Title should have between 5 and 50 characters."));
    }
    
    private Course cloneCourse(Course original) {
        Course cloned = new Course();
        cloned.setId(original.getId());
        cloned.setTitle(original.getTitle());
        cloned.setLastUpdate(original.getLastUpdate());
        cloned.setCourseFeedback(original.getCourseFeedback());
        cloned.setCourseDescription(original.getCourseDescription());
        cloned.setSubtitles(original.getSubtitles());
        cloned.setCategories(original.getCategories());
        cloned.setPrice(original.getPrice());
        cloned.setLanguages(original.getLanguages());
        return cloned;
    }

}