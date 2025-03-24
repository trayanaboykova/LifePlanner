package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookAlreadyApprovedException extends DomainException {
    private final UUID bookId;
    public BookAlreadyApprovedException(UUID bookId) {
        super("Book " + bookId + " is already approved");
        this.bookId = bookId;
    }
}
