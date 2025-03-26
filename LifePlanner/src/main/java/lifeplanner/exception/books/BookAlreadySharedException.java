package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookAlreadySharedException extends DomainException {
    private final UUID bookId;
    public BookAlreadySharedException(UUID bookId) {
        super("Book " + bookId + " is already shared");
        this.bookId = bookId;
    }
}