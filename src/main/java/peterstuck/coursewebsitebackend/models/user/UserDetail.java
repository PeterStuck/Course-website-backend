package peterstuck.coursewebsitebackend.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "website_user_detail")
public class UserDetail {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column
    private String profileImageUrl;

    @Column
    private String portfolioUrl;

    @Column
    private String twitterProfileUrl;

    @Column
    private String facebookProfileUrl;

    @Column
    private String linkedInProfileUrl;

    @Column
    private String youtubeProfileUrl;

}
