package peterstuck.coursewebsitebackend.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import peterstuck.coursewebsitebackend.models.user.User;

import javax.persistence.EntityManager;

@Repository
public class UserRepository {

    @Autowired
    private EntityManager manager;

    public User findByEmail(String email) throws UsernameNotFoundException {
        try {
            return manager.createQuery("from User where email=:email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User with email: " + email + " not found.");
        }
    }

    public User save(User user) {
        manager.persist(user);

        return user;
    }

}
