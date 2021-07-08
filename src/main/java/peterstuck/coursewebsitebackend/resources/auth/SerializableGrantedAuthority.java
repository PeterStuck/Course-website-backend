package peterstuck.coursewebsitebackend.resources.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
@AllArgsConstructor
public class SerializableGrantedAuthority implements GrantedAuthority {

    private String role;

    @Override
    public String getAuthority() {
        return role;
    }
}
