package peterstuck.coursewebsitebackend.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Entity
@Table(name = "course_description")
@ApiModel(description = "Details about course")
public class CourseDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @ApiModelProperty(notes = "course duration in hours", required = true)
    private double duration;

    @Column
    @ApiModelProperty(notes = "course sneak peak", required = true)
    private String shortDescription;

    @Column
    private String longDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    @ApiModelProperty(notes = "topics that will be in course")
    private List<String> mainTopics;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    @ApiModelProperty(notes = "additional pre-requirements info for students")
    private List<String> requirements;

    public CourseDescription(double duration, String shortDescription, String longDescription) {
        this.duration = duration;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double courseLength) {
        this.duration = courseLength;
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
