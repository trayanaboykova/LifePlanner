package lifeplanner.exception.media;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MediaPendingApprovalException extends RuntimeException {
    private final UUID mediaId;
    public MediaPendingApprovalException(UUID mediaId) {
        super("Media " + mediaId + " is pending approval and cannot be shared.");
        this.mediaId = mediaId;
    }
}