package peterstuck.coursewebsitebackend.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.resources.TestRequestUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AccessTest {

    @Autowired
    private MockMvc mvc;

    private TestRequestUtils tru;

    @BeforeEach
    void setUp() {
        tru = new TestRequestUtils(null, mvc, "");
    }

//    @Test
    void shouldReturnStatus403WhenTryToAccessSwaggerDocsWithoutAuthorization() throws Exception {
        tru.makeRequestToGetSingleItem("/v3/api-docs", status().isForbidden());
        tru.makeRequestToGetSingleItem("/v2/api-docs", status().isForbidden());
        tru.makeRequestToGetSingleItem("/swagger-ui/", status().isForbidden());
    }

    @Test
    void shouldReturnStatus403WhenTryToAccessActuatorWithoutAuthorization() throws Exception {
        tru.makeRequestToGetSingleItem("/actuator", status().isForbidden());
        tru.makeRequestToGetSingleItem("/actuator/health", status().isForbidden());
    }

}
