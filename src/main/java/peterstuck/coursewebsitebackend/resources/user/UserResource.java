package peterstuck.coursewebsitebackend.resources.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.resources.auth.JwtToken;
import peterstuck.coursewebsitebackend.services.user.UserService;
import peterstuck.coursewebsitebackend.utils.JsonFilter;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService service;

    private final String FILTER_NAME = "UserFilter";

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void registerNewUser(@Valid @RequestBody User user) {
        service.register(user);
    }

    @PutMapping
    public JwtToken updateUser(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody User user) throws UsernameNotFoundException {
        String newToken = service.update(authHeader, user);
        return new JwtToken(newToken);
    }

    // TODO fix direct User class filtering
    @GetMapping
    public Object getUserInfo(@RequestHeader("Authorization") String authHeader) throws UsernameNotFoundException, JsonProcessingException {
        User user = service.getUserInfo(authHeader);
        String obj = JsonFilter.castObjectToJsonString(user, FILTER_NAME, new String[] { "userActivity", "roles", "password" });

//        return (User) JsonFilter.filterFields(user, FILTER_NAME, new String[] { "userActivity", "roles", "password" });
        return new ObjectMapper().readValue(obj, Object.class);
    }

}
