package peterstuck.coursewebsitebackend.resources.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.exceptions.UsernameNotUniqueException;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.resources.auth.JwtToken;
import peterstuck.coursewebsitebackend.services.user.UserService;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserResource {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService service;

    private final String FILTER_NAME = "JsonFilter";

    @Operation(summary = "register new user", description = "Operation available for everyone.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid user data",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void registerNewUser(
            @Parameter(description = "valid new user object", required = true)
            @Valid @RequestBody User user) throws UsernameNotUniqueException {
        service.register(user);
    }

    @Operation(summary = "updates user data", description = "User identification is being proceed based on passed JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid user data",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
    })
    @PutMapping
    public JwtToken updateUser(
            @Parameter(description = "authorization request header", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "valid updated user object", required = true)
            @Valid @RequestBody User user) throws UsernameNotFoundException {
        String newToken = service.update(authHeader, user);
        return new JwtToken(newToken);
    }

    @Operation(summary = "returns user data", description = "User identification is being proceed based on passed JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data (without vulnerable data)",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid JWT",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
    })
    @GetMapping
    public User getUserInfo(
            @Parameter(description = "authorization request header", required = true)
            @RequestHeader("Authorization") String authHeader) throws UsernameNotFoundException, JsonProcessingException {
        User user = service.getUserInfo(authHeader);

        String[] exceptFields = new String[] {
                // user
                "userActivity",
                "roles",
                "password",
                // course
                "price",
                "courseDescription",
                "courseFeedback"
        };

        return (User) JsonFilter.filterFields(user, FILTER_NAME, exceptFields);
    }

}
