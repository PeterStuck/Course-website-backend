package peterstuck.coursewebsitebackend.models.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peterstuck.coursewebsitebackend.models.user.UserActivity;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "Comment content", required = true)
    @Column
    @NotBlank(message = "Comment description is mandatory.")
    @Size(max = 200, message = "Comment maximum length is 200.")
    private String description;

    @Schema(required = true)
    @Column
    @NotNull(message = "Rate in comment is mandatory.")
    @Min(value = 1, message = "Minimum star value for curse is one.")
    @Max(value = 5, message = "Max star value for curse is five.")
    private Double rate;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "course_feedback_id")
    private CourseFeedback courseFeedback;

    @JsonIgnoreProperties(value = {"comment", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_activity_id")
    private UserActivity author;

}
