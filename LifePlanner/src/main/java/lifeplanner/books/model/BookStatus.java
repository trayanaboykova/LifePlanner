package lifeplanner.books.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookStatus {
    READ("Read"),
    CURRENTLY_READING("Currently Reading"),
    WANT_TO_READ("Want to Read");

    private final String label;
}

