package peterstuck.coursewebsitebackend.resources.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.CategoryNotFoundException;
import peterstuck.coursewebsitebackend.models.course.Category;
import peterstuck.coursewebsitebackend.services.category.CategoryService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/categories")
@Api(value = "Category", tags = { "Category" })
public class CategoryResource {

    @Autowired
    @Qualifier("categoryServiceImpl")
    private CategoryService service;

    @GetMapping
    @ApiOperation(value = "returns main categories", notes = """
            Main categories are categories without parent category (0 as parent category ID).
            Returns status 200 (OK) when categories are available, 204 (NO CONTENT) when it's empty list.
            """)
    public ResponseEntity<List<Category>> getMainCategories() {
        return getResponseAndStatus(service.getMainCategories());
    }

    @GetMapping("/{parentCategoryId}")
    @ApiOperation(value = "returns child categories", notes = """
            Returns child categories based on parent category ID.
            Returns status 200 (OK) when categories are available, 204 (NO CONTENT) when it's empty list.
            """)
    public ResponseEntity<List<Category>> getChildCategories(
            @ApiParam(required = true)
            @PathVariable int parentCategoryId) {
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
    @ApiOperation(value = "creates new category", notes = """
        Returns status 201 (CREATED) when category was successfully created, 400 (BAD REQUEST) when there's an error in category data.
        Operation available only for page administrator.
        """)
    public EntityModel<Category> createCategory(
            @ApiParam(required = true)
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "valid new Category object", required = true)
            @Valid @RequestBody Category category) {
        Category savedCategory = service.save(category);

        return getCategoryEntityModel(savedCategory);
    }

    @PutMapping("/{categoryId}")
    @ApiOperation(value = "updates existing category", notes = """
            Returns status 200 (OK) when category was updated successfully,
            400 (BAD REQUEST) when there's an error in category data or 404 when category was not found.
            Operation available only for page administrator.
            """)
    public EntityModel<Category> updateCategory(
            @ApiParam(required = true)
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "id of category that is going to be updated", required = true)
            @PathVariable int categoryId,
            @ApiParam(value = "valid Category object with updated data", required = true)
            @Valid @RequestBody Category category) throws CategoryNotFoundException {
        Category updated = service.update(categoryId, category);

        return getCategoryEntityModel(updated);
    }

    private EntityModel<Category> getCategoryEntityModel(Category category) {
        EntityModel<Category> model = EntityModel.of(category);
        WebMvcLinkBuilder linkToMainCategories = linkTo(methodOn(this.getClass()).getMainCategories());
        model.add(linkToMainCategories.withRel("main-categories-link"));

        WebMvcLinkBuilder linkToChildCategories = linkTo(methodOn(this.getClass()).getChildCategories(category.getId()));
        model.add(linkToChildCategories.withRel("child-categories-link"));

        return model;
    }

    @DeleteMapping("/{categoryId}")
    @ApiOperation(value = "deletes existing category", notes = """
            Returns status 200 (OK) when category was successfully deleted, 404 when category to delete was not found.
            Operation available only for page administrator.
            """)
    public String deleteCategory(
            @ApiParam(required = true)
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "id of category to delete", required = true)
            @PathVariable int categoryId) throws CategoryNotFoundException {
        service.delete(categoryId);
        return "Category with id: " + categoryId + " successfully deleted.";
    }

}
