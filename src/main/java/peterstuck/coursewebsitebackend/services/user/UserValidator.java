package peterstuck.coursewebsitebackend.services.user;

import peterstuck.coursewebsitebackend.exceptions.UsernameNotUniqueException;

public interface UserValidator {

    /**
     * @param username User email
     * @throws UsernameNotUniqueException when User with given username (email) already exists
     */
    void checkIsUsernameUnique(String username) throws UsernameNotUniqueException;

}
