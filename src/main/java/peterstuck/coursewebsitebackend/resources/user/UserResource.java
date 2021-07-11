package peterstuck.coursewebsitebackend.resources.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.resources.auth.JwtToken;
import peterstuck.coursewebsitebackend.services.user.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService service;

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

    @GetMapping
    public User getUserInfo(@RequestHeader("Authorization") String authHeader) throws UsernameNotFoundException {
        return service.getUserInfo(authHeader);
    }

}
