package peterstuck.coursewebsitebackend.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.factory.CourseFactory;
import peterstuck.coursewebsitebackend.repositories.CourseRepository;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class CourseRestControllerTest {

//    @TestConfiguration
//    static class CourseServiceImplTestContextConfiguration {
//
//        @Bean
//        public CourseService courseServiceImpl() {
//            return new CourseServiceImpl();
//        }
//
//    }

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CourseRepository repository;


    @BeforeEach
    void setUp() {
//        when(repository.findAll())
//                .thenReturn();
    }

    @Test
    void test() throws CourseNotFoundException {

    }

}