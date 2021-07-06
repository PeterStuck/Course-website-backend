package peterstuck.coursewebsitebackend.resources.category;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.models.Category;
import peterstuck.coursewebsitebackend.repositories.CategoryRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryRepository repository;

    @BeforeEach
    void setUp() {
    }

    private List<Category> makeRequestToGetCourses;

}