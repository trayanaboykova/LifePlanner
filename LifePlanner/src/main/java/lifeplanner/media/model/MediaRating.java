package lifeplanner.media.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaRating {
    ONE("⭐"),
    TWO("⭐⭐"),
    THREE("⭐⭐⭐"),
    FOUR("⭐⭐⭐⭐"),
    FIVE("⭐⭐⭐⭐⭐");

    private final String label;
}