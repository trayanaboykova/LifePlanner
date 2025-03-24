package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookNotFoundException extends DomainException {
    private final UUID bookId;

    public BookNotFoundException(UUID bookId) {
        super("Book not found with ID: " + bookId);
        this.bookId = bookId;
    }

}