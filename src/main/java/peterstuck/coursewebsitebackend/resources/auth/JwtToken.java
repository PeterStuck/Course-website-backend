package peterstuck.coursewebsitebackend.resources.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "Wrapper for JWT")
public class JwtToken {

    @ApiModelProperty(required = true)
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }
}
