package peterstuck.coursewebsitebackend.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @ApiModelProperty(required = true)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column
    private List<Language> languages;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<Language> subtitles;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;


    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private List<Comment> comments;

    @Column(name = "last_update")
    @ApiModelProperty(notes = "Date of last course update in long format. Automatically created when course is being created and every course update")
    private Long lastUpdate;

    @Column
    @ApiModelProperty(required = true)
    private Double price;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_description_id")
    @ApiModelProperty(required = true)
    private CourseDescription courseDescription;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<Double> rates;

    @ApiModelProperty(value = "Average rate from all rates for course", required = true)
    private double avgRate;

    @ApiModelProperty(required = true)
    private int ratesCount;

    public Course() {
        this.rates = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.lastUpdate = new Date().getTime();
    }
}
