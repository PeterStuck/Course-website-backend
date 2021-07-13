package peterstuck.coursewebsitebackend.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import peterstuck.coursewebsitebackend.exceptions.UsernameNotUniqueException;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.repositories.UserRepository;

@Component
public class UserValidatorImpl implements UserValidator {

    @Autowired
    private UserRepository repository;

    @Override
    public void checkIsUsernameUnique(String username) throws UsernameNotUniqueException {
        try {
            User user = repository.findByEmail(username);
            if (user != null) throw new UsernameNotUniqueException("Email: " + username + " is already in use. Try again with another one or sign in.");
        } catch (UsernameNotFoundException ignored) {}
    }
}
