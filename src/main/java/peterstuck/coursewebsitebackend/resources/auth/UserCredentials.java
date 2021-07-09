package peterstuck.coursewebsitebackend.resources.auth;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value = "Credentials wrapper")
public class UserCredentials {

    @Email(message = "Wrong email syntax.")
    private String email;

    @NotBlank(message = "Password is mandatory.")
    private String password;

}
