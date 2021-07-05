package peterstuck.coursewebsitebackend.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.models.CourseDescription;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;

import java.util.Date;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository repository;

    @Override
    @Transactional
    public List<Course> findAll() {
        List<Course> courses = repository.findAll();
        courses.forEach(course -> {
            initializeLazyObjects(course);
            computeAvgAndCountOfRates(course);
        });

        return courses;
    }

    @Override
    @Transactional
    public Course findById(int id) throws CourseNotFoundException {
        Course course = repository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with id: " + id + " not found!"));

        initializeLazyObjects(course);
        computeAvgAndCountOfRates(course);

        return course;
    }

    private void computeAvgAndCountOfRates(Course course) {
        List<Double> rates = course.getRates();

        double avg = rates.stream().mapToDouble(f -> f).sum() / rates.size();
        course.setAvgRate(avg);
        course.setRatesCount(rates.size());
    }

    private void initializeLazyObjects(Course course) {
        Hibernate.initialize(course.getSubtitles());

        if (course.getCourseDescription() != null) {
            Hibernate.initialize(course.getCourseDescription());
            Hibernate.initialize(course.getCourseDescription().getMainTopics());
            Hibernate.initialize(course.getCourseDescription().getRequirements());
        }
    }

    @Override
    @Transactional
    public Course save(Course course) {
        return repository.save(course);
    }

    @Override
    @Transactional
    public Course update(int id, Course updated) throws CourseNotFoundException {
        Course course = this.findById(id);
        updateCourse(course, updated);
        this.save(course);

        return updated;
    }

    private void updateCourse(Course original, Course updated) {
        // user cannot override comments, rates, avgRate, ratesCount
        original.setTitle(updated.getTitle());
        original.setLastUpdate(new Date().getTime());
        original.setSubtitles(updated.getSubtitles());
        original.setCategories(updated.getCategories());
        original.setPrice(updated.getPrice());
        original.setLanguages(updated.getLanguages());

        updateCourseDescription(original.getCourseDescription(), updated.getCourseDescription());
    }

    private void updateCourseDescription(CourseDescription original, CourseDescription updated) {
        original.setDuration(updated.getDuration());
        original.setShortDescription(updated.getShortDescription());
        original.setLongDescription(updated.getLongDescription());
        original.setMainTopics(updated.getMainTopics());
        original.setRequirements(updated.getRequirements());
    }

    @Override
    @Transactional
    public void delete(int id) throws CourseNotFoundException {
        Course course = this.findById(id);
        repository.delete(course);
    }

}
