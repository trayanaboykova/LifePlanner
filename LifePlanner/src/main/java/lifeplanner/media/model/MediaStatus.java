package lifeplanner.media.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaStatus {
    WATCHED("Watched"),
    WANT_TO_WATCH("Want to watch"),;

    private final String label;
}