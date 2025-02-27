package lifeplanner.media.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MediaLikesId implements Serializable {

    private UUID mediaId;

    private UUID userId;

    public MediaLikesId(UUID mediaId, UUID userId) {
        this.mediaId = mediaId;
        this.userId = userId;
    }

    public MediaLikesId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MediaLikesId that = (MediaLikesId) o;
        return Objects.equals(mediaId, that.mediaId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaId, userId);
    }
}
