package peterstuck.coursewebsitebackend.resources.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CategoryNotFoundException;
import peterstuck.coursewebsitebackend.models.Category;
import peterstuck.coursewebsitebackend.services.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryResource {

    @Autowired
    @Qualifier("categoryServiceImpl")
    private CategoryService service;

    @GetMapping
    public ResponseEntity<List<Category>> getMainCategories() {
        return getResponseAndStatus(service.getMainCategories());
    }

    @GetMapping("/{parentCategoryId}")
    public ResponseEntity<List<Category>> getChildCategories(@PathVariable int parentCategoryId) {
        return getResponseAndStatus(service.getChildCategories(parentCategoryId));
    }

    private ResponseEntity<List<Category>> getResponseAndStatus(List<Category> categories) {
        return new ResponseEntity<>(
                categories,
                (categories.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody Category category) {
        return service.save(category);
    }

    @PutMapping("/{categoryId}")
    public Category updateCategory(@PathVariable int categoryId, @RequestBody Category category) throws CategoryNotFoundException {
        return service.update(categoryId, category);
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable int categoryId) throws CategoryNotFoundException {
        service.delete(categoryId);
        return "Category with id: " + categoryId + " successfully deleted.";
    }

}
