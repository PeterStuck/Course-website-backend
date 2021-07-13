package peterstuck.coursewebsitebackend.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.exceptions.UsernameNotUniqueException;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserActivity;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.utils.JwtUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static peterstuck.coursewebsitebackend.utils.ObjectInitializer.initializeUserObject;
import static peterstuck.coursewebsitebackend.utils.ObjectUpdater.updateUser;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    @Qualifier("userValidatorImpl")
    private UserValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    /**
     * email acts as username
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void register(User user) throws UsernameNotUniqueException {
        validator.checkIsUsernameUnique(user.getEmail());

        user.setUserActivity(new UserActivity());

        repository.save(user);
    }

    @Override
    @Transactional
    /**
     * Returns new JWT after user update
     */
    public String update(String token, User updatedUser) throws UsernameNotFoundException {
        User actualUser = extractUsernameAndGetUser(token);
        updateUser(actualUser, updatedUser);
        repository.save(actualUser);

        return jwtUtil.generateToken(this.loadUserByUsername(actualUser.getEmail()));
    }

    @Override
    @Transactional
    public User getUserInfo(String token) throws UsernameNotFoundException {
        User user = extractUsernameAndGetUser(token);
        initializeUserObject(user);

        return user;
    }

    private User extractUsernameAndGetUser(String token) {
        token = token.substring(7);
        User user = repository.findByEmail(jwtUtil.extractUsername(token));
        if (user == null) {
            throw new UsernameNotFoundException("Wrong token.");
        }
        return user;
    }

}
