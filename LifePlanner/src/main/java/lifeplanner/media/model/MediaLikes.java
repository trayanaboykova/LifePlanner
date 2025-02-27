package lifeplanner.media.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaLikes {
    @EmbeddedId
    private MediaLikesId id;

    @MapsId("mediaId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id")
    private Media media;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
