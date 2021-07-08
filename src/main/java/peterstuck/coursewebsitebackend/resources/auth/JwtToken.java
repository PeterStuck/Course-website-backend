package peterstuck.coursewebsitebackend.resources.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtToken {

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }
}
