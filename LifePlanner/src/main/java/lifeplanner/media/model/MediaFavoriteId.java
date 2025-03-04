package lifeplanner.media.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MediaFavoriteId implements Serializable {
    private UUID mediaId;
    private UUID userId;

    public MediaFavoriteId(UUID mediaId, UUID userId) {
        this.mediaId = mediaId;
        this.userId = userId;
    }

    public MediaFavoriteId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MediaFavoriteId that = (MediaFavoriteId) o;
        return Objects.equals(mediaId, that.mediaId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaId, userId);
    }
}