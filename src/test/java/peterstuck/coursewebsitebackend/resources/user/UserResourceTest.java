package peterstuck.coursewebsitebackend.resources.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserActivity;
import peterstuck.coursewebsitebackend.models.user.UserDetail;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.resources.TestRequestUtils;
import peterstuck.coursewebsitebackend.services.user.UserService;
import peterstuck.coursewebsitebackend.utils.JwtUtil;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserResourceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository repository;

    @MockBean
    private JwtUtil jwtUtil;

    private TestRequestUtils tru;

    private User validTestUser;
    private User invalidUser;

    private final String BASE_PATH = "/api/users";

    @BeforeEach
    void setUp() {
        tru = new TestRequestUtils(User.class, mvc, "");

        initializeTestUsers();
    }

    private void initializeTestUsers() {
        validTestUser = new User();
        validTestUser.setEmail("test@email.com");
        validTestUser.setFirstName("First");
        validTestUser.setLastName("Last");
        validTestUser.setPassword(passwordEncoder.encode("test"));
        validTestUser.setUserDetail(new UserDetail());
        validTestUser.setOwnCourses(Collections.emptyList());

        invalidUser = new User();
        invalidUser.setEmail("testemail.com");
        invalidUser.setFirstName("fIRst");
        invalidUser.setLastName("tEsT");
        invalidUser.setPassword(passwordEncoder.encode("test"));
        invalidUser.setUserDetail(null);
        invalidUser.setOwnCourses(Collections.emptyList());
    }

    @Test
    void shouldAllowToCreateNewUserAndReturnStatus201() throws Exception {
        tru.makePostRequest(BASE_PATH, validTestUser, status().isCreated());
    }

    @Test
    void shouldReturnStatus400AndErrorMessagesWhenNewUserObjectIsInvalid() throws Exception {
        String response = tru.makePostRequest(BASE_PATH, invalidUser, status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("email"));
        assertThat(response, containsString("firstName"));
        assertThat(response, containsString("lastName"));
        assertThat(response, containsString("userDetail"));

        assertThat(response, containsString("Bad email syntax."));
        assertThat(response, containsString("Name should have first capital letter."));
        assertThat(response, containsString("Surname should have first capital letter."));
        assertThat(response, containsString("Each user should have user details associated."));
    }

    @WithMockUser
    @Test
    void shouldAllowToUpdateUserAndReturnStatus200() throws Exception {
        when(jwtUtil.extractUsername(any())).thenReturn(validTestUser.getEmail());
        when(jwtUtil.generateToken(any())).thenReturn("test token");
        when(repository.findByEmail(any())).thenReturn(validTestUser);

        validTestUser.setFirstName("Updated");
        tru.makePutRequest(BASE_PATH, validTestUser, status().isOk());

        assertThat(repository.findByEmail(validTestUser.getEmail()).getFirstName(), equalTo("Updated"));
    }

    @WithMockUser
    @Test
    void shouldReturnStatus400WhenUserWithExtractedUsernameNotExistsDuringUpdate() throws Exception {
        tru.makePutRequest(BASE_PATH, validTestUser, status().isBadRequest());
    }

    @WithMockUser
    @Test
    void shouldReturnUserDataWithUserDetailsAndStatus200() throws Exception {
        when(repository.findByEmail(any())).thenReturn(validTestUser);

        String response = tru.makeRequestToGetSingleItem(BASE_PATH, status().isOk()).getContentAsString();
        User responseUser = new ObjectMapper().readValue(response, User.class);

        assertThat(responseUser.getFirstName(), equalTo(validTestUser.getFirstName()));
        assertThat(responseUser.getLastName(), equalTo(validTestUser.getLastName()));
        assertThat(responseUser.getEmail(), equalTo(validTestUser.getEmail()));
        assertThat(responseUser.getPurchasedCourses(), equalTo(validTestUser.getPurchasedCourses()));
        assertThat(responseUser.getOwnCourses(), equalTo(validTestUser.getOwnCourses()));
    }

}