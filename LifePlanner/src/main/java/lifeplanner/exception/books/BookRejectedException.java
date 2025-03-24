package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookRejectedException extends DomainException {
  private final UUID bookId;
  public BookRejectedException(UUID bookId) {
    super("Book " + bookId + " was rejected and cannot be shared. Please edit and resubmit.");
    this.bookId = bookId;
  }
}