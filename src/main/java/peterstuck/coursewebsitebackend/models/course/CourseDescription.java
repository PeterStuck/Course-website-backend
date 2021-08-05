package peterstuck.coursewebsitebackend.models.course;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "Details about course")
@JsonFilter("JsonFilter")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_description")
public class CourseDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Schema(description = "course duration in hours", required = true)
    @Column
    @NotNull(message = "Duration is mandatory.")
    private Double duration;

    @Schema(description = "course sneak peak", required = true)
    @Column
    @NotBlank(message = "Short description is mandatory.")
    @Size(max = 200, message = "Short description length should not be greater than 200 characters.")
    private String shortDescription;

    @Column
    @Size(max = 500, message = "Long description length should not be greater than 500 characters.")
    private String longDescription;

    @Schema(description = "topics that will be in course")
    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<String> mainTopics;

    @Schema(description = "additional pre-requirements info for students")
    @ElementCollection(fetch = FetchType.LAZY)
    @Column
    private List<String> requirements;

}
