package peterstuck.coursewebsitebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import peterstuck.coursewebsitebackend.models.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
