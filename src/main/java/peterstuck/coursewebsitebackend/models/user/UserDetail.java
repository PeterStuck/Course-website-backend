package peterstuck.coursewebsitebackend.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "website_user_detail")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
