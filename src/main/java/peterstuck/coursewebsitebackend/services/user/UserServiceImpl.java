package peterstuck.coursewebsitebackend.services.user;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserActivity;
import peterstuck.coursewebsitebackend.repositories.RoleRepository;
import peterstuck.coursewebsitebackend.repositories.UserRepository;
import peterstuck.coursewebsitebackend.utils.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    /**
     * email acts as username
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

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
    public void register(User user) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setUserActivity(new UserActivity());
        user.setRoles(new ArrayList<>(Collections.singletonList(userRole)));
        userRepository.save(user);
    }

    @Override
    @Transactional
    /**
     * Returns new JWT after user update
     */
    public String update(String token, User updatedUser) throws UsernameNotFoundException {
        token = token.substring(7);
        User actualUser = userRepository.findByEmail(jwtUtil.extractUsername(token));
        if (actualUser == null) {
            throw new UsernameNotFoundException("Wrong token.");
        }

        updateUser(actualUser, updatedUser);
        userRepository.save(actualUser);

        return jwtUtil.generateToken(this.loadUserByUsername(actualUser.getEmail()));
    }

    private void updateUser(User original, User updated) {
        original.setEmail(updated.getEmail());
        original.setFirstName(updated.getFirstName());
        original.setLastName(updated.getLastName());
        original.setUserDetail(updated.getUserDetail());
    }

    @Override
    @Transactional
    public User getUserInfo(String token) throws UsernameNotFoundException {
        token = token.substring(7);

        User user = userRepository.findByEmail(jwtUtil.extractUsername(token));
        initUserCollections(user);

        return user;
    }

    private void initUserCollections(User user) {
        Hibernate.initialize(user.getRoles());
        Hibernate.initialize(user.getPurchasedCourses());
        Hibernate.initialize(user.getOwnCourses());
        Hibernate.initialize(user.getUserDetail());
        Hibernate.initialize(user.getUserActivity());
    }
}
