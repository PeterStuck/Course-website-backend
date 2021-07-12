package peterstuck.coursewebsitebackend.services.course;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.exceptions.NotAnAuthorException;
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
    public List<Course> findAll() {
        List<Course> courses = courseRepository.findAll();
        courses.forEach(course -> {
            initializeLazyObjects(course);
            computeAvgAndCountOfRates(course);
        });

        return courses;
    }

    @Override
    @Transactional
    public Course findById(Long id) throws CourseNotFoundException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with id: " + id + " not found!"));

        initializeLazyObjects(course);
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

    private void initializeLazyObjects(Course course) {
        Hibernate.initialize(course.getSubtitles());
        Hibernate.initialize(course.getCategories());
        Hibernate.initialize(course.getAuthors());
        Hibernate.initialize(course.getCourseFeedback().getComments());

        if (course.getCourseDescription() != null) {
            Hibernate.initialize(course.getCourseDescription());
            Hibernate.initialize(course.getCourseDescription().getMainTopics());
            Hibernate.initialize(course.getCourseDescription().getRequirements());
        }
    }

    @Override
    @Transactional
    public Course save(Course course) {
        course.setCourseFeedback(new CourseFeedback());
        return courseRepository.save(course);
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

    /**
     * It should not be possible to override courseFeedback
     * @param original course to update
     * @param updated course with updated data
     */
    private void updateCourse(Course original, Course updated) {
        original.setTitle(updated.getTitle());
        original.setLastUpdate(new Date().getTime());
        original.setSubtitles(updated.getSubtitles());
        original.setCategories(updated.getCategories());
        original.setPrice(updated.getPrice());
        original.setLanguages(updated.getLanguages());

        updateCourseDescription(original.getCourseDescription(), updated.getCourseDescription());
    }

    /**
     * @param original course description to update
     * @param updated course description with updated data
     */
    private void updateCourseDescription(CourseDescription original, CourseDescription updated) {
        original.setDuration(updated.getDuration());
        original.setShortDescription(updated.getShortDescription());
        original.setLongDescription(updated.getLongDescription());
        original.setMainTopics(updated.getMainTopics());
        original.setRequirements(updated.getRequirements());
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
