package peterstuck.coursewebsitebackend.resources.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.services.auth.AuthService;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthResource {

    @Autowired
    private AuthService service;

    @Operation(summary = "returns generated JWT",
            description = """
                Generate JSON Web Token based on credentials that user provides. 
                This token is used to authenticate user on particular endpoints.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad credentials",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class)) })
    })
    @PostMapping("/authenticate")
    public JwtToken authenticateUserAndReturnToken(
            @Parameter(description = "consists of email and plain password", required = true)
            @Valid  @RequestBody UserCredentials credentials) {
        String token = service.authenticateUser(credentials);

        return new JwtToken(token);
    }

}
