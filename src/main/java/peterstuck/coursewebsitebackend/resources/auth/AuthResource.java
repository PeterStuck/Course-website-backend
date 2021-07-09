package peterstuck.coursewebsitebackend.resources.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peterstuck.coursewebsitebackend.services.auth.AuthService;

@RestController
@RequestMapping("/api/auth")
@Api(value = "Authentication", tags = { "Authentication" })
public class AuthResource {

    @Autowired
    private AuthService service;

    @PostMapping("/authenticate")
    @ApiOperation(value = "returns generated JWT",
            notes = """
                Generate JSON Web Token based on credentials that user provides. 
                This token is used to authenticate user on particular endpoints.
                When credentials are invalid returns status 400 and message.
            """)
    public JwtToken authenticateUserAndReturnToken(
            @ApiParam(value = "consists of email and plain password", required = true)
            @RequestBody UserCredentials credentials) {
        String token = service.authenticateUser(credentials);

        return new JwtToken(token);
    }

}
