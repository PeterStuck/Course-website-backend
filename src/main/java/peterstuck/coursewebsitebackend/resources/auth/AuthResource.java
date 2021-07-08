package peterstuck.coursewebsitebackend.resources.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peterstuck.coursewebsitebackend.services.auth.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    @Autowired
    private AuthService service;

    @PostMapping("/authenticate")
    public JwtToken authenticateUserAndReturnToken(@RequestBody UserCredentials credentials) {
        String token = service.authenticateUser(credentials);

        return new JwtToken(token);
    }

}
