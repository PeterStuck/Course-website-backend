package peterstuck.coursewebsitebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import peterstuck.coursewebsitebackend.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
