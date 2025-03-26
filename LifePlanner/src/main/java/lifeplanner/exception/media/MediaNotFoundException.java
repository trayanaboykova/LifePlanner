package lifeplanner.exception.media;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MediaNotFoundException extends RuntimeException {
  private final UUID mediaId;
    public MediaNotFoundException(UUID mediaId) {
      super("Media not found with ID: " + mediaId);
        this.mediaId = mediaId;
    }
}
