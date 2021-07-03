package peterstuck.coursewebsitebackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import peterstuck.coursewebsitebackend.exceptions.CourseInvalidDataException;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository repository;

    @Override
    public List<Course> findAll() {
        return repository.findAll();
    }

    @Override
    public Course findById(int id) throws CourseNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with id: " + id + " not found!"));
    }

    @Override
    public Course save(Course course) {
        return repository.save(course);
    }

    @Override
    public Course update(int id, Course updated) throws CourseInvalidDataException, CourseNotFoundException {
        if (id != updated.getId())
            throw new CourseInvalidDataException("Path ID and request body ID must match!");
        // check if course exists
        Course course = this.findById(id);
        updateCourse(course, updated);
        this.save(course);

        return updated;
    }

    @Override
    public void delete(int id) throws CourseNotFoundException {
        Course course = this.findById(id);
        repository.delete(course);
    }

    private void updateCourse(Course original, Course updated) {
        original.setTitle(updated.getTitle());
        original.setLastUpdate(updated.getLastUpdate());
        original.setComments(updated.getComments());
        original.setCourseDescription(updated.getCourseDescription());
        original.setSubtitles(updated.getSubtitles());
        original.setCategories(updated.getCategories());
        original.setPrice(updated.getPrice());
        original.setLanguages(updated.getLanguages());
        original.setRates(updated.getRates());
    }
}
