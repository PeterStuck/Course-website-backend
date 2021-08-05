package peterstuck.coursewebsitebackend.resources.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categories")
public class CategoryResource {

    @Autowired
    @Qualifier("categoryServiceImpl")
    private CategoryService service;

    @Operation(summary = "returns main categories", description = "Main categories are categories without parent category (0 as parent category id).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "204", description = "No categories found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
    })
    @GetMapping
    public ResponseEntity<List<Category>> getMainCategories() {
        return getResponseAndStatus(service.getMainCategories());
    }

    @Operation(summary = "returns child categories", description = "Returns child categories based on parent category id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Child categories found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "204", description = "No child categories found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
    })
    @GetMapping("/{parentCategoryId}")
    public ResponseEntity<List<Category>> getChildCategories(
            @Parameter(required = true)
            @PathVariable int parentCategoryId) {
        return getResponseAndStatus(service.getChildCategories(parentCategoryId));
    }

    private ResponseEntity<List<Category>> getResponseAndStatus(List<Category> categories) {
        return new ResponseEntity<>(
                categories,
                (categories.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
        );
    }


    @Operation(summary = "creates new category", description = "Operation available only for page administrator.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid category data or not an admin",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Category> createCategory(
            @Parameter(required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "valid new Category object", required = true)
            @Valid @RequestBody Category category) {
        Category savedCategory = service.save(category);

        return getCategoryEntityModel(savedCategory);
    }


    @Operation(summary = "updates category", description = "Operation available only for page administrator.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid category data or not an admin",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = "Category with supplied id not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
    })
    @PutMapping("/{categoryId}")
    public EntityModel<Category> updateCategory(
            @Parameter(required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "id of category that is going to be updated", required = true)
            @PathVariable int categoryId,
            @Parameter(description = "valid Category object with updated data", required = true)
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

    @Operation(summary = "deletes category", description = "Operation available only for page administrator.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "403", description = "Not an admin",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
            @ApiResponse(responseCode = "404", description = "Category with supplied id not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)) }),
    })
    @DeleteMapping("/{categoryId}")
    public String deleteCategory(
            @Parameter(required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "id of category to delete", required = true)
            @PathVariable int categoryId) throws CategoryNotFoundException {
        service.delete(categoryId);
        return "Category with id: " + categoryId + " successfully deleted.";
    }

}
