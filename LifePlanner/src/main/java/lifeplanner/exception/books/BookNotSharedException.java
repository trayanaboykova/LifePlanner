package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookNotSharedException extends DomainException {
  private final UUID bookId;
  public BookNotSharedException(UUID bookId) {
    super("Book " + bookId + " is not currently shared");
    this.bookId = bookId;
  }
}