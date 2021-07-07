package peterstuck.coursewebsitebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import peterstuck.coursewebsitebackend.models.course.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
