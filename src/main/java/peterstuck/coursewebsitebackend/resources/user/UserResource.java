package peterstuck.coursewebsitebackend.resources.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "Users", tags = { "Users" })
public class UserResource {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService service;

    private final String FILTER_NAME = "JsonFilter";

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "returns status 201 (CREATED) when user was created successfully", notes = """
            User object must be valid to proceed. 
            Returns status 400 (BAD REQUEST) when user data is invalid and error messages.
            Operation available for everyone.
            """)
    public void registerNewUser(
            @ApiParam(value = "valid new user object", required = true)
            @Valid @RequestBody User user) throws UsernameNotUniqueException {
        service.register(user);
    }

    @PutMapping
    @ApiOperation(value = "returns status 200 (OK) when user was successfully updated", notes = """
            User identification is being proceed based on passed JWT.
            When user was found in database and updated data is valid object is being updated and saving changes.
            Returns status 400 (BAD REQUEST) when user data is invalid and error messages.
            """)
    public JwtToken updateUser(
            @ApiParam(value = "authorization request header", required = true)
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "valid updated user object", required = true)
            @Valid @RequestBody User user) throws UsernameNotFoundException {
        String newToken = service.update(authHeader, user);
        return new JwtToken(newToken);
    }

    @GetMapping
    @ApiOperation(value = "returns particular user data", notes = """
            User identification is being proceed based on passed JWT.
            Response excludes sensitive data and non important fields, ex. user roles, password etc.
            When everything proceed successfully returns status 200 (OK), otherwise 400 (BAD REQUEST).
            """)
    public User getUserInfo(
            @ApiParam(value = "authorization request header", required = true)
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
