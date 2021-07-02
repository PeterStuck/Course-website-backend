package peterstuck.coursewebsitebackend.models;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Entity
@Table(name = "course_description")
public class CourseDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private double courseLength;

    @Column
    private String shortDescription;

    @Column
    private String longDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<String> mainTopics;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<String> requirements;

    public CourseDescription(double courseLength, String shortDescription, String longDescription) {
        this.courseLength = courseLength;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCourseLength() {
        return courseLength;
    }

    public void setCourseLength(double courseLength) {
        this.courseLength = courseLength;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getMainTopics() {
        return mainTopics;
    }

    public void setMainTopics(List<String> mainTopics) {
        this.mainTopics = mainTopics;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }
}
