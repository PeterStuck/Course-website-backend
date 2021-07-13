package peterstuck.coursewebsitebackend.services.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.exceptions.NotAnAuthorException;
import peterstuck.coursewebsitebackend.exceptions.UserNotExistsException;
import peterstuck.coursewebsitebackend.models.course.Comment;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.models.course.CourseDescription;
import peterstuck.coursewebsitebackend.models.course.CourseFeedback;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.utils.JwtUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static peterstuck.coursewebsitebackend.utils.ObjectInitializer.initializeCourseObject;
import static peterstuck.coursewebsitebackend.utils.ObjectUpdater.updateCourse;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public List<Course> findAll(String keyword) {
        List<Course> courses = courseRepository.findAll();
        if (keyword != null)
            courses = filterCoursesByTitle(courses, keyword);

        courses.forEach(course -> {
            initializeCourseObject(course);
            computeAvgAndCountOfRates(course);
        });

        return courses;
    }

    private List<Course> filterCoursesByTitle(List<Course> courses, String keyword) {
        return courses.stream()
                .filter(course -> checkCourseTitleContainsKeyword(course, keyword))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Course> findAllByCategory(String keyword, int categoryId) {
        return this.findAll(keyword)
                .stream()
                .filter(course -> checkCourseHasCategoryWithId(course, categoryId))
                .collect(Collectors.toList());
    }

    private boolean checkCourseTitleContainsKeyword(Course course, String keyword) {
        return course.getTitle().toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean checkCourseHasCategoryWithId(Course course, int categoryId) {
        return course.getCategories()
                .stream()
                .anyMatch(category -> category.getId() == categoryId);
    }

    @Override
    @Transactional
    public Course findById(Long id) throws CourseNotFoundException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with id: " + id + " not found!"));

        initializeCourseObject(course);
        computeAvgAndCountOfRates(course);

        return course;
    }

    private void computeAvgAndCountOfRates(Course course) {
        var courseFeedback = course.getCourseFeedback();
        var courseComments = course.getCourseFeedback().getComments();

        double avg = courseComments.stream().mapToDouble(Comment::getRate).sum() / courseComments.size();
        courseFeedback.setAvgRate(avg);
        courseFeedback.setRatesCount(courseComments.size());
    }

    @Override
    @Transactional
    public Course save(Course course, String token) throws UserNotExistsException {
        course.setCourseFeedback(new CourseFeedback());
        checkAuthors(course, token);

        return courseRepository.save(course);
    }

    /**
     * Checks if selected authors exists in database, adds requester as one of authors when not selected
     * @throws UserNotExistsException when one of selected authors not exists in database
     */
    private void checkAuthors(Course course, String token) throws UserNotExistsException {
        for (User author : course.getAuthors()) {
            var tmpAuthor = userRepository.findByEmail(author.getEmail());
            if (tmpAuthor == null)
                throw new UserNotExistsException("User with email: " + author.getEmail() + " not exists.");
        }

        User user = userRepository.findByEmail(jwtUtil.extractUsername(token));
        if (!course.getAuthors().contains(user) && user != null) course.getAuthors().add(user);
    }

    @Override
    @Transactional
    public Course update(Long id, String token, Course updated) throws CourseNotFoundException, NotAnAuthorException {
        Course course = this.findById(id);
        checkIsAnAuthorOrThrowException(course, token);

        updateCourse(course, updated);
        courseRepository.save(course);

        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id, String token) throws CourseNotFoundException, NotAnAuthorException {
        Course course = this.findById(id);
        checkIsAnAuthorOrThrowException(course, token);
        courseRepository.delete(course);
    }

    /**
     * @throws NotAnAuthorException when requester was not recognized as author of course based on JWT.
     */
    private void checkIsAnAuthorOrThrowException(Course course, String token) throws NotAnAuthorException {
        token = token.substring(7);
        User user = userRepository.findByEmail(jwtUtil.extractUsername(token));

        boolean isAnAuthor = course.getAuthors()
                .stream()
                .anyMatch(author -> author.equals(user));

        if (!isAnAuthor) throw new NotAnAuthorException("You are allow to update or delete only own courses.");
    }

}
