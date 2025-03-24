package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookAlreadyRejectedException extends DomainException {
    private final UUID bookId;
    public BookAlreadyRejectedException(UUID bookId) {
        super("Book " + bookId + " is already rejected");
        this.bookId = bookId;
    }
}