package peterstuck.coursewebsitebackend.resources.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.services.auth.UserService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public UserDetails authenticateUserAndReturnToken(@RequestBody UserCredentials credentials) {
        UserDetails user = userService.findUserByEmail(credentials.getEmail());

        return user;
    }

}
