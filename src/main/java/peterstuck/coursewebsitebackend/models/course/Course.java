package peterstuck.coursewebsitebackend.models.course;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import peterstuck.coursewebsitebackend.models.user.User;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Schema(description = "Basic information about course")
@JsonFilter("JsonFilter")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Schema(required = true)
    @Column
    @NotBlank(message = "Title is mandatory.")
    @Size(min = 5, max = 50, message = "Title should have between 5 and 50 characters.")
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column
    private Set<Language> languages;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private Set<Language> subtitles;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @Schema(description = "Date of last course update in long format. Automatically created when course is being created and every course update")
    @Column(name = "last_update")
    private Long lastUpdate;

    @Schema(required = true)
    @Column
    @NotNull(message = "Price is mandatory.")
    private Double price;

    @Schema(required = true)
    @JsonIgnoreProperties(value = {"course", "hibernateLazyInitializer"})
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_description_id")
    @Valid
    @NotNull(message = "Course must have a description.")
    private CourseDescription courseDescription;

    @Schema(description = "Contains all related feedback from users to course.", required = true)
    @JsonIgnoreProperties(value = {"course", "hibernateLazyInitializer"})
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_feedback_id")
    private CourseFeedback courseFeedback;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @ManyToMany(mappedBy = "purchasedCourses", cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private List<User> students;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(
            name = "course_website_user",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "website_user_id")
    )
    private List<User> authors;

    public Course() {
        categories = new ArrayList<>();
        authors = new ArrayList<>();
        lastUpdate = new Date().getTime();
    }
}
