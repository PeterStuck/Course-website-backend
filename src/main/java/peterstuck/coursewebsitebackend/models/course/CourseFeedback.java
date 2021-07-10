package peterstuck.coursewebsitebackend.models.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "course_feedback")
public class CourseFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "courseFeedback")
    private List<Comment> comments;

    @OneToMany(mappedBy = "courseFeedback")
    private List<Rate> rates;

    @ApiModelProperty(value = "Average rate from all rates for course")
    private double avgRate;

    private int ratesCount;

    public CourseFeedback() {
        comments = new ArrayList<>();
        rates = new ArrayList<>();

        avgRate = 0.0;
        ratesCount = 0;
    }
}
