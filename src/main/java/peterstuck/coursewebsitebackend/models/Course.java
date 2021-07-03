package peterstuck.coursewebsitebackend.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonFilter("CourseFilter")
@AllArgsConstructor
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column
    private String title;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<Double> rates;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column
    private List<Language> languages;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<Language> subtitles;

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
    private Long lastUpdate;

    @Column
    private Double price;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_description_id")
    private CourseDescription courseDescription;

    public Course() {
        this.rates = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.lastUpdate = new Date().getTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Double> getRates() {
        return rates;
    }

    public void setRates(List<Double> rates) {
        this.rates = rates;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Language> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Language> subtitles) {
        this.subtitles = subtitles;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public CourseDescription getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(CourseDescription courseDescription) {
        this.courseDescription = courseDescription;
    }
}
