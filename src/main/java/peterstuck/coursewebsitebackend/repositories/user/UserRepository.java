package peterstuck.coursewebsitebackend.repositories.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import peterstuck.coursewebsitebackend.models.user.Role;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.repositories.RoleRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;

@Repository
public class UserRepository {

    @Autowired
    private EntityManager manager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public User findByEmail(String email) throws UsernameNotFoundException {
        try {
            return manager.createQuery("from User where email=:email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User with email: " + email + " not found.");
        }
    }

    /**
     * Every new User gets role USER as default role.
     */
    public User save(User user, RegistrationType type) {
        user.setPassword(encoder.encode(user.getPassword()));

        switch (type) {
            case DEFAULT -> {
                Role userRole = roleRepository.findByName("ROLE_USER");
                user.setRoles(new ArrayList<>(Collections.singletonList(userRole)));

                manager.persist(user);
            }
            case CUSTOM -> {
                manager.persist(user);
            }
            default -> System.out.println("Registration type not recognized.");
        }
        return user;
    }

}
