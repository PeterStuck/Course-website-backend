package peterstuck.coursewebsitebackend.services.category;

import peterstuck.coursewebsitebackend.exceptions.CategoryNotFoundException;
import peterstuck.coursewebsitebackend.models.Category;

import java.util.*;

public interface CategoryService {

    /**
     * Main categories with 0 as parent category ID haven't got parent category.
     */
    List<Category> getMainCategories();

    List<Category> getChildCategories(int parentCategoryId);

    Category save(Category category);

    Category update(int categoryId, Category updated) throws CategoryNotFoundException;

    void delete(int categoryId) throws CategoryNotFoundException;

}
