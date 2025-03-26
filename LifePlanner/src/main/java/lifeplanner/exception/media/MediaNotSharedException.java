package lifeplanner.exception.media;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;
@Getter
public class MediaNotSharedException extends RuntimeException {
    private final UUID mediaId;
    public MediaNotSharedException(UUID mediaId) {
        super("Media " + mediaId + " is not currently shared");
        this.mediaId = mediaId;
    }
}