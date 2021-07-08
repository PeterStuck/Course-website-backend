package peterstuck.coursewebsitebackend.resources.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCredentials {

    private String email;

    private String password;

}
