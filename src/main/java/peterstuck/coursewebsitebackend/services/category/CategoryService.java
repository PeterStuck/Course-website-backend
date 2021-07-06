package peterstuck.coursewebsitebackend.services.category;

import peterstuck.coursewebsitebackend.exceptions.CategoryNotFoundException;
import peterstuck.coursewebsitebackend.models.Category;

import java.util.*;

public interface CategoryService {

    List<Category> getMainCategories();

    List<Category> getChildCategories(int parentCategoryId);

    Category save(Category category);

    Category update(Category updated) throws CategoryNotFoundException;

    void delete(int categoryId) throws CategoryNotFoundException;

}
