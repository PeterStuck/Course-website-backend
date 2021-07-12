package peterstuck.coursewebsitebackend.models.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import peterstuck.coursewebsitebackend.models.course.Course;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("UserFilter")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "website_user")
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Email(message = "Bad email syntax.")
    @ApiModelProperty(notes = "Must have email syntax.", required = true)
    private String email;

    @Column
    @Pattern(regexp = "^([A-Z][A-Za-z]*)?$", message = "Name should have first capital letter.")
    @ApiModelProperty(notes = "Optional user description.")
    private String firstName;

    @Column
    @Pattern(regexp = "^([A-Z][A-Za-z])?$", message = "Surname should have first capital letter.")
    @ApiModelProperty(notes = "Optional user description.")
    private String lastName;

    @Column
    @Size(min = 8, message = "Password should have at least 8 characters of length.")
    @ApiModelProperty(notes = "Password is stored as bcrypt.", required = true)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "website_user_role",
            joinColumns = @JoinColumn(name = "website_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ApiModelProperty(notes = "Every new user gets role USER as default.", required = true)
    private List<Role> roles;

    // TODO check for lazy exception
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "website_user_course",
            joinColumns = @JoinColumn(name = "website_user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> purchasedCourses;

    // TODO check for lazy exception
    @ManyToMany(mappedBy = "authors", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Course> ownCourses;

    @JsonIgnoreProperties(value = {"website_user", "hibernateLazyInitializer"})
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "website_user_detail_id")
    @NotNull(message = "Each user should have user details associated.")
    @ApiModelProperty(notes = "Social media and more details about user.", required = true)
    private UserDetail userDetail;

    @JsonIgnoreProperties(value = {"website_user", "hibernateLazyInitializer"})
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_activity_id")
    @ApiModelProperty(notes = "Created with user.", required = true)
    private UserActivity userActivity;

    public User() {
        roles = new ArrayList<>();
        purchasedCourses = new ArrayList<>();
        ownCourses = new ArrayList<>();
    }

}
