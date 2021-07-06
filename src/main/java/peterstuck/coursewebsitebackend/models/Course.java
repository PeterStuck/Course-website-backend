package peterstuck.coursewebsitebackend.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonFilter("CourseFilter")
@Data
@AllArgsConstructor
@Entity
@Table(name = "course")
@ApiModel(description = "Basic information about course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

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
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
//    @Size(min = 1, message = "Course must have at least one category selected.")
    private List<Category> categories;

    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private List<Comment> comments;

    @Column(name = "last_update")
    @ApiModelProperty(notes = "Date of last course update in long format. Automatically created when course is being created and every course update")
    private Long lastUpdate;

    @Column
    @ApiModelProperty(required = true)
    @NotNull(message = "Price is mandatory.")
    private Double price;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_description_id")
    @ApiModelProperty(required = true)
    @Valid
    @NotNull(message = "Course must have a description.")
    @JsonIgnoreProperties(value = {"course", "hibernateLazyInitializer"})
    private CourseDescription courseDescription;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<Double> rates;

    @ApiModelProperty(value = "Average rate from all rates for course")
    private double avgRate;

    private int ratesCount;

    public Course() {
        rates = new ArrayList<>();
        comments = new ArrayList<>();
        categories = new ArrayList<>();
        lastUpdate = new Date().getTime();

        avgRate = 0.0;
        ratesCount = 0;
    }
}
