package lifeplanner.exception.media;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MediaAlreadySharedException extends RuntimeException {
    private final UUID mediaId;
    public MediaAlreadySharedException(UUID mediaId) {
        super("Media " + mediaId + " is already shared");
        this.mediaId = mediaId;
    }
}