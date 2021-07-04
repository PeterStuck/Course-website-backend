package peterstuck.coursewebsitebackend.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
