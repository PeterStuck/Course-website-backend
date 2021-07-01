package peterstuck.coursewebsitebackend.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peterstuck.coursewebsitebackend.models.Course;
import peterstuck.coursewebsitebackend.models.Rating;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseRestController {

    @Autowired
    private CourseRepository repository;

    @GetMapping
    public Course getAllCourses() {
        Course course = new Course("TESTOWY KURS", 20.00);

        Optional<Rating> rating = Arrays.stream(Rating.values()).filter(rate -> rate.starValue == 1.5).findFirst();

        if (rating.isEmpty()) {
            throw new IllegalArgumentException();
        }

        course.setLastUpdate(new Date().getTime());

        course.setRates(Arrays.asList(
                Rating.ONE.starValue,
                Rating.ONE_AND_HALF.starValue,
                Rating.TWO.starValue,
                Rating.FOUR_AND_HALF.starValue,
                rating.get().starValue
        ));

        repository.save(course);

        return course;
    }

}
