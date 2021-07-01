package peterstuck.coursewebsitebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import peterstuck.coursewebsitebackend.models.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}
