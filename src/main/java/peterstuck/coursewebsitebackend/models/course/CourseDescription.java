package peterstuck.coursewebsitebackend.models.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
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
    @NotNull(message = "Duration is mandatory.")
    private Double duration;

    @Column
    @ApiModelProperty(notes = "course sneak peak", required = true)
    @NotBlank(message = "Short description is mandatory.")
    @Size(max = 200, message = "Short description length should not be greater than 200 characters.")
    private String shortDescription;

    @Column
    @Size(max = 500, message = "Long description length should not be greater than 500 characters.")
    private String longDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    @ApiModelProperty(notes = "topics that will be in course")
    private List<String> mainTopics;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    @ApiModelProperty(notes = "additional pre-requirements info for students")
    private List<String> requirements;

}
