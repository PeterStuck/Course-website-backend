package peterstuck.coursewebsitebackend.models.course;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

@JsonFilter("JsonFilter")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "course")
@ApiModel(description = "Basic information about course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    @NotBlank(message = "Title is mandatory.")
    @Size(min = 5, max = 50, message = "Title should have between 5 and 50 characters.")
    @ApiModelProperty(required = true)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column
    private Set<Language> languages;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private Set<Language> subtitles;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
//    @Size(min = 1, message = "Course must have at least one category selected.")
    private List<Category> categories;

    @Column(name = "last_update")
    @ApiModelProperty(notes = "Date of last course update in long format. Automatically created when course is being created and every course update")
    private Long lastUpdate;

    @Column
    @ApiModelProperty(required = true)
    @NotNull(message = "Price is mandatory.")
    private Double price;

    @JsonIgnoreProperties(value = {"course", "hibernateLazyInitializer"})
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_description_id")
    @ApiModelProperty(required = true)
    @Valid
    @NotNull(message = "Course must have a description.")
    private CourseDescription courseDescription;

    @JsonIgnoreProperties(value = {"course", "hibernateLazyInitializer"})
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_feedback_id")
    @ApiModelProperty(notes = "Contains all related feedback from users to course.", required = true)
    private CourseFeedback courseFeedback;

    @JsonIgnore
    @ManyToMany(mappedBy = "purchasedCourses")
    private List<User> students;

    @ManyToMany(fetch = FetchType.LAZY)
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
