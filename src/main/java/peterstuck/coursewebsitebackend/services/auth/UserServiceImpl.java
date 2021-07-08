package peterstuck.coursewebsitebackend.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.resources.auth.SerializableGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

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
        return roles.stream().map(role -> new SerializableGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User register(User user) {
        return null;
    }

    @Override
    @Transactional
    public User update(User user) {
        return null;
    }

    @Override
    @Transactional
    public void delete(Long id) {

    }
}
