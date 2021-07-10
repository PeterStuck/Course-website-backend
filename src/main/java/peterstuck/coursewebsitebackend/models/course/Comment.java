package peterstuck.coursewebsitebackend.models.course;

import lombok.Data;
import lombok.NoArgsConstructor;
import peterstuck.coursewebsitebackend.models.user.User;
import peterstuck.coursewebsitebackend.models.user.UserActivity;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "course_feedback_id")
    private CourseFeedback courseFeedback;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_activity_id")
    private UserActivity author;

}
