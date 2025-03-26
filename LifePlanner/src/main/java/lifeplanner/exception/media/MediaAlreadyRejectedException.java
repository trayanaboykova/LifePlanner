package lifeplanner.exception.media;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MediaAlreadyRejectedException extends RuntimeException {
    private final UUID mediaId;
    public MediaAlreadyRejectedException(UUID mediaId) {
        super("Media " + mediaId + " is already rejected");
        this.mediaId = mediaId;
    }
}