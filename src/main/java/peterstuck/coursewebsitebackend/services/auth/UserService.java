package peterstuck.coursewebsitebackend.services.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import peterstuck.coursewebsitebackend.models.user.User;

public interface UserService extends UserDetailsService {

    User register(User user);

    User update(User user);

    void delete(Long id);

}
