package lifeplanner.exception.media;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MediaRejectedException extends RuntimeException {
    private final UUID mediaId;
    public MediaRejectedException(UUID mediaId) {
        super("Media " + mediaId + " was rejected and cannot be shared. Please edit and resubmit.");
        this.mediaId = mediaId;
    }
}