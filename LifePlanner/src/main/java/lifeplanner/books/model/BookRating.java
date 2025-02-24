package lifeplanner.books.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookRating {
    ONE("1 star - ⭐"),
    TWO("2 stars - ⭐⭐"),
    THREE("3 stars - ⭐⭐⭐"),
    FOUR("4 stars - ⭐⭐⭐⭐"),
    FIVE("5 stars - ⭐⭐⭐⭐⭐");

    private final String label;
}

