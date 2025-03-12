package lifeplanner.books.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookRating {
    ONE("⭐"),
    TWO("⭐⭐"),
    THREE("⭐⭐⭐"),
    FOUR("⭐⭐⭐⭐"),
    FIVE("⭐⭐⭐⭐⭐");

    private final String label;
}