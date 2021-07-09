package peterstuck.coursewebsitebackend.resources.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserDetail;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.resources.TestRequestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder encoder;

    private TestRequestUtils tru;
    private User testUser;

    private final String AUTHENTICATE_PAH = "/api/auth/authenticate";

    @BeforeEach
    void setUp() {
        tru = new TestRequestUtils(null, mvc, "");

        initializeTestUser();
    }

    private void initializeTestUser() {
        testUser = new User();
        testUser.setEmail("email@email.com");
        testUser.setFirstName("name");
        testUser.setLastName("last");
        testUser.setPassword(encoder.encode("user"));
        testUser.setPurchasedCourses(Collections.emptyList());
        testUser.setRoles(Arrays.asList(
                new Role("ROLE_ADMIN"),
                new Role("ROLE_USER")
        ));
        testUser.setUserDetail(new UserDetail());
    }

    @Test
    void whenUserDataIsValidShouldReturnJWT() throws Exception {
        String testEmail = "test@test.com";
        when(userRepository.findByEmail(testEmail)).thenReturn(testUser);
        when(authManager.authenticate(any())).thenReturn(null);

        String response = tru.makePostRequest(AUTHENTICATE_PAH, new UserCredentials(testEmail, "test"), status().isOk()).getContentAsString();

        verify(userRepository).findByEmail(testEmail);
        assertThat(response, containsString("token"));
    }

    @Test
    void whenUserCredentialsInvalidReturnStatus400AndMessage() throws Exception {
        String response = tru.makePostRequest(AUTHENTICATE_PAH, new UserCredentials("wrongemailsynatax", ""), status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("email"));
        assertThat(response, containsString("Wrong email syntax."));
        assertThat(response, containsString("password"));
        assertThat(response, containsString("Password is mandatory."));
    }

    @Test
    void whenUserNotExistsShouldReturnStatus400AndMessage() throws Exception {
        when(authManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        String response = tru.makePostRequest(AUTHENTICATE_PAH, new UserCredentials("fake@fake.com", "fake"), status().isBadRequest()).getContentAsString();

        assertThat(response, containsString("timestamp"));
        assertThat(response, containsString("message"));
        assertThat(response, containsString("Bad credentials."));
    }

}