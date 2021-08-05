package peterstuck.coursewebsitebackend.models.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import peterstuck.coursewebsitebackend.models.course.Course;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("JsonFilter")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "website_user")
@Schema
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Email(message = "Bad email syntax.")
    @Schema(description = "Must have email syntax. Acts as username.", required = true)
    private String email;

    @Column
    @Pattern(regexp = "^([A-Z][A-Za-z]*)?$", message = "Name should have first capital letter.")
    @Schema(description = "Optional user description.")
    private String firstName;

    @Column
    @Pattern(regexp = "^([A-Z][A-Za-z]*)?$", message = "Surname should have first capital letter.")
    @Schema(description = "Optional user description.")
    private String lastName;

    @Column
    @NotBlank(message = "Password is mandatory.")
    @Size(min = 8, message = "Password should have at least 8 characters of length.")
    @Schema(description = "Password is stored as bcrypt.", required = true)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "website_user_role",
            joinColumns = @JoinColumn(name = "website_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Schema(description = "Every new user gets role USER as default.", required = true)
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "website_user_course",
            joinColumns = @JoinColumn(name = "website_user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> purchasedCourses;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<Course> ownCourses;

    @JsonIgnoreProperties(value = {"website_user", "hibernateLazyInitializer"})
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "website_user_detail_id")
    @NotNull(message = "Each user should have user details associated.")
    @Schema(description = "Social media and more details about user.", required = true)
    private UserDetail userDetail;

    @JsonIgnoreProperties(value = {"website_user", "hibernateLazyInitializer"})
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_activity_id")
    @Schema(description = "Created with user.", required = true)
    private UserActivity userActivity;

    public User() {
        roles = new ArrayList<>();
        purchasedCourses = new ArrayList<>();
        ownCourses = new ArrayList<>();
    }

}
