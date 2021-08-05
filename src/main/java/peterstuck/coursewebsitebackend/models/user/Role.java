package peterstuck.coursewebsitebackend.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
@Schema(description = "Used in Spring Security to determine access to particular resources.")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Pattern(regexp = "^ROLE_[A-Z]{2,}$", message = "Role name should match pattern: ROLE_NAME, ex. ROLE_ADMIN.")
    @Schema(description = "Role name. Should be uppercase and start with 'ROLE_'.", required = true)
    private String name;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<User> users;

    public Role(String name) {
        this.name = name;
    }

}
