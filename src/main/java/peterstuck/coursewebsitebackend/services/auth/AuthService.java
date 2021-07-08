package peterstuck.coursewebsitebackend.services.auth;

import peterstuck.coursewebsitebackend.resources.auth.UserCredentials;

public interface AuthService {

    /**
     * @return generated JWT
     */
    String authenticateUser(UserCredentials credentials);

    boolean validateToken(String token);

}
