package peterstuck.coursewebsitebackend.resources.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.resources.TestRequestUtils;
import peterstuck.coursewebsitebackend.models.course.Category;
import peterstuck.coursewebsitebackend.repositories.CategoryRepository;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryRepository repository;

    private List<Category> testCategories;
    private Category testCategory;
    private TestRequestUtils tru;

    private final String BASE_PATH = "/api/categories";

    @BeforeEach
    void setUp() {
        tru = new TestRequestUtils(Category.class, mvc, "");

        initializeTestCategory();
        initializeTestCategories();
    }

    private void initializeTestCategory() {
        testCategory = new Category("TEST CATEGORY", 0);
    }

    private void initializeTestCategories() {
        testCategories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            var cat = new Category("CATEGORY " + (i+1), i);
            testCategories.add(cat);
        }
    }

    @Test
    void shouldReturnMainCategoriesOnly() throws Exception {
        when(repository.findAll()).thenReturn(testCategories);

        List<Category> categories = (List<Category>) tru.makeRequestToGetItems(BASE_PATH, status().isOk());

        verify(repository).findAll();
        assertThat(categories, hasSize(1));
    }

    @Test
    void whenMainCategoryListIsEmptyShouldReturnEmptyList() throws Exception {
        assertThat(tru.makeRequestToGetItems(BASE_PATH, status().isNoContent()), equalTo(Collections.emptyList()));
    }

    @Test
    void shouldReturnChildCategoriesOnly() throws Exception {
        int parentCatId = 1;
        testCategories.get(2).setParentCategoryId(1);
        when(repository.findAll()).thenReturn(testCategories);

        List<Category> categories = (List<Category>) tru.makeRequestToGetItems(BASE_PATH + "/" + parentCatId, status().isOk());

        verify(repository).findAll();
        assertThat(categories, hasSize(2));
        assertThat(categories.get(0).getName(), equalTo("CATEGORY 2"));
    }

    @Test
    void whenChildCategoryListIsEmptyShouldReturnEmptyListAndStatusNoContent() throws Exception {
        assertThat(tru.makeRequestToGetItems(BASE_PATH + "/" + 999, status().isNoContent()), equalTo(Collections.emptyList()));
    }

    @Test
    void whenNewCategoryObjectIsValidShouldBeAbleToSaveAndReturnStatusCreated() throws Exception {
        when(repository.save(testCategory)).then(invocationOnMock -> {
           testCategories.add(testCategory);
            return testCategory;
        });

        var response = tru.makePostRequest(BASE_PATH, testCategory, status().isCreated());
        Category category = TestRequestUtils.mapper.readValue(response.getContentAsString(), Category.class);

        verify(repository).save(testCategory);
        assertThat(testCategories, hasSize(6));
        assertThat(category.getName(), equalTo(testCategory.getName()));
    }

    @Test
    void categoryShouldBeUpdatedWhenNewObjectIsValidAndStatusOk() throws Exception {
        int catId = 1;
        testCategory.setId(catId);
        when(repository.findById(catId)).thenReturn(Optional.ofNullable(testCategory));

        testCategory.setName("UPDATED NAME");
        testCategory.setParentCategoryId(10);

        var response = tru.makePutRequest(BASE_PATH + "/" + catId, testCategory, status().isOk());
        Category updatedCategory = TestRequestUtils.mapper.readValue(response.getContentAsString(), Category.class);

        verify(repository).findById(catId);
        assertThat(updatedCategory.getName(), equalTo("UPDATED NAME"));
        assertThat(updatedCategory.getParentCategoryId(), equalTo(10));
    }

    @Test
    void shouldReturnStatus404AndMessageWhenCategoryWithGivenIdNotFound() throws Exception {
        var response = tru.makePutRequest(BASE_PATH + "/" + 562, testCategory, status().isNotFound());

        assertThat(response.getContentAsString(), containsString("Category with id: 562 not found."));
        assertThat(response.getContentAsString(), containsString("message"));
        assertThat(response.getContentAsString(), containsString("timestamp"));
    }

    @Test
    void shouldDeleteCategoryIfExistsAndStatus200() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.ofNullable(testCategory));

        var response = tru.makeDeleteRequest(BASE_PATH + "/1", status().isOk());

        verify(repository).delete(testCategory);
        assertThat(response.getContentAsString(), containsString("Category with id: 1 successfully deleted."));
    }

    @Test
    void whenInvalidCategoryObjectPassedThenReturnStatus400AndFieldErrorMessages() throws Exception {
        Category invalidCategory = new Category("", -1);
        String response = tru.makePostRequest(BASE_PATH, invalidCategory, status().isBadRequest()).getContentAsString();

        // check field names with errors are returned
        assertThat(response, containsString("name"));
        assertThat(response, containsString("parentCategoryId"));
        // check error messages are returned
        assertThat(response, containsString("Parent category ID cannot be negative."));
        assertThat(response, containsString("Category name should have at least 4 characters."));
    }

    @Test
    void shouldReturnStatus404WhenCategoryNotFoundDuringDeleteRequest() throws Exception {
        String response = tru.makeDeleteRequest(BASE_PATH + "/" + 123, status().isNotFound()).getContentAsString();

        assertThat(response, containsString("Category with id: 123 not found."));
        assertThat(response, containsString("message"));
        assertThat(response, containsString("timestamp"));
    }

}