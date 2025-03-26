package lifeplanner.exception.media;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MediaAlreadyApprovedException extends RuntimeException {
    private final UUID mediaId;
    public MediaAlreadyApprovedException(String message, UUID mediaId) {
        super("Media " + mediaId + " is already approved");
        this.mediaId = mediaId;
    }
}