package peterstuck.coursewebsitebackend.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import peterstuck.coursewebsitebackend.models.course.Course;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "website_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Email(message = "Bad email syntax.")
    private String email;

    @Column
    @Pattern(regexp = "^[A-Z][A-Za-z]+$", message = "Name should have first capital letter.")
    private String firstName;

    @Column
    @Pattern(regexp = "^[A-Z][A-Za-z]+$", message = "Surname should have first capital letter.")
    private String lastName;

    @Column
    @Size(min = 8, message = "Password should have at least 8 characters of length.")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties(value = {"website_user", "hibernateLazyInitializer"})
    @JoinTable(
            name = "website_user_role",
            joinColumns = @JoinColumn(name = "website_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
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
    private List<Course> ownCourses;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "website_user_detail_id")
    @NotNull(message = "Each user should have user details associated.")
    private UserDetail userDetail;

    @Getter(AccessLevel.NONE)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_activity_id")
    @NotNull(message = "Each user should have user activities associated.")
    private UserActivity userActivity;

}
