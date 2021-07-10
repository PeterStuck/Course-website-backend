package peterstuck.coursewebsitebackend.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import peterstuck.coursewebsitebackend.models.course.Comment;
import peterstuck.coursewebsitebackend.models.course.Course;
import peterstuck.coursewebsitebackend.models.course.Rate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Rate> rates;

    public UserActivity() {
        comments = new ArrayList<>();
        rates = new ArrayList<>();
    }
}
