package peterstuck.coursewebsitebackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.models.course.factory.CourseFactory;
import peterstuck.coursewebsitebackend.models.course.factory.CourseDescriptionFactory;
import peterstuck.coursewebsitebackend.models.course.*;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserActivity;
import peterstuck.coursewebsitebackend.models.user.UserDetail;
import peterstuck.coursewebsitebackend.repositories.*;
import peterstuck.coursewebsitebackend.repositories.user.RegistrationType;
import peterstuck.coursewebsitebackend.repositories.user.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Profile("!test")
@Component
public class InitRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(InitRunner.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUsers();

        initializeCategories();
        initializeCourses();

//        initializeUserPurchasedCourses();
    }

    void initializeCategories() {
        categoryRepository.save(new Category("Programowanie", 0));
        categoryRepository.save(new Category("Biznes", 0));
        categoryRepository.save(new Category("Projektowanie", 0));
        categoryRepository.save(new Category("Tworzenie stron internetowych", 1));
        categoryRepository.save(new Category("Tworzenie gier", 1));
        categoryRepository.save(new Category("Prowadzenie firmy", 2));
        categoryRepository.save(new Category("Komunikacja i zarządzanie", 2));
        categoryRepository.save(new Category("Narzędzia projektowe", 3));

        categoryRepository.findAll().forEach(category -> logger.info("CREATED CATEGORY {}", category.getName()));
    }

    void initializeCourses() {
        var course = createSampleCourse();
        Comment sampleComment = createSampleComment(course, userRepository.findByEmail("email@email.com"), Rating.FIVE.starValue);
        course.getCourseFeedback().getComments().add(sampleComment);

        var course2 = createSampleCourse();

        courseRepository.save(course);
        courseRepository.save(course2);

        courseRepository.findAll().forEach(c -> logger.info("CREATED COURSE {}", c.getTitle()));
    }

    private Comment createSampleComment(Course course, User author, Double rate) {
        var sampleComment = new Comment();
        sampleComment.setAuthor(author.getUserActivity());
        sampleComment.setDescription("Some comment");
        sampleComment.setCourseFeedback(course.getCourseFeedback());
        sampleComment.setRate(rate);
        return sampleComment;
    }

    private Course createSampleCourse() {
        var sampleCourse =  CourseFactory.createCourse(
                "Kurs Tworzenia Stron WWW w HTML i CSS",
                59.99,
                CourseDescriptionFactory.createCourseDescription(
                        48.5,
                        "Naucz się HTML 5, CSS 3, JS, XML, jQuery, AJAX, Responsive web design od podstaw. Wyjaśnię Ci wszystko od A do Z!",
                        """
                                Naucz się wszystkiego od zera. Obal mit, że tworzenie stron internetowych jest trudne. Twórz własne strony WWW od A do Z.\s                         
                                Będziesz znał i rozumiał HTML 5, CSS 3, JS, XML, jQuery oraz AJAX.\s                           
                                Co więcej nauczysz się Responsive Web Design czyli techniki, która sprawi, że Twoja strona będzie wyglądać idealnie na smartfonach jak i na komputerach stacjonarnych.\s                     
                                """,
                        Arrays.asList(
                                "tworzyć od zera stronę WWW w HTML z rozwijanym menu w CSS",
                                "czym są, jakie są oraz jak korzystać z tagów HTML/selektorów CSS",
                                "nowości związane z HTML 5: tagi semantyczne, eventy, atrybuty",
                                "różnice między HTML, XHTML, HTML 5, CSS i CSS 3"
                        ),
                        Collections.singletonList("Wszystko wyjaśnione jest w kursie. Nie musisz posiadać żadnych wiadomości. Wystarczą dobre chęci :)")),
                new ArrayList<>(Arrays.asList(categoryRepository.findById(1).get()))
        );
        sampleCourse.setCourseFeedback(new CourseFeedback());
        sampleCourse.setAuthors(new ArrayList<>(Collections.singletonList(userRepository.findByEmail("email@email.com"))));

        return sampleCourse;
    }

    void initializeRoles() {
        var user_role = new Role("ROLE_USER");
        var developer_role = new Role("ROLE_DEVELOPER");
        var admin_role = new Role("ROLE_ADMIN");

        roleRepository.save(user_role);
        roleRepository.save(developer_role);
        roleRepository.save(admin_role);

        roleRepository.findAll().forEach(role -> logger.info("CREATED ROLE {}", role.getName()));
    }

    void initializeUsers() {
        var user = createSampleUser(
                "email@email.com",
                "user",
                new UserActivity(),
                Collections.singletonList(roleRepository.findById(1).get()),
                new UserDetail());

        var admin = createSampleUser(
                "admin@email.com",
                "admin",
                new UserActivity(),
                Collections.singletonList(roleRepository.findById(3).get()),
                new UserDetail());

        userRepository.save(user, RegistrationType.DEFAULT);
        userRepository.save(admin, RegistrationType.CUSTOM);

        logger.info("CREATED USER {}", user.getEmail());
        logger.info("CREATED USER {}", admin.getEmail());
    }

    private User createSampleUser(String email, String plainPassword, UserActivity activity, List<Role> roles, UserDetail detail) {
        var user = new User();
        user.setEmail(email);
        user.setFirstName("Name");
        user.setLastName("Last");
        user.setPassword(plainPassword);
        user.setUserActivity(activity);
        user.setRoles(roles);
        user.setUserDetail(detail);

        return user;
    }

    protected void initializeUserPurchasedCourses() {
        User user = userRepository.findByEmail("admin@email.com");
        user.setPurchasedCourses(new ArrayList<>(Arrays.asList(
                courseRepository.getById(1L),
                courseRepository.getById(2L)
        )));

        userRepository.save(user, RegistrationType.CUSTOM);

        logger.info("USER " + user.getEmail() + " PURCHASED SOME COURSES");
    }

}
