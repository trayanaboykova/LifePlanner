package lifeplanner.media.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeMedia {
    MOVIE("Movie"),
    TV_SHOW("TV Show");

    private final String label;
}
