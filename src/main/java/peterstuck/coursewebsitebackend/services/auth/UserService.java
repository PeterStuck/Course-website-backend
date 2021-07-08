package peterstuck.coursewebsitebackend.services.auth;

import org.springframework.security.core.userdetails.UserDetails;
import peterstuck.coursewebsitebackend.models.user.User;

public interface UserService {

    UserDetails findUserByEmail(String email);

    User register(User user);

    User update(User user);

    void delete(Long id);

}
