package lifeplanner.exception.books;

import lifeplanner.exception.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookPendingApprovalException extends DomainException {
  private final UUID bookId;
    public BookPendingApprovalException(UUID bookId) {
      super("Book " + bookId + " is pending approval and cannot be shared.");
      this.bookId = bookId;
    }
}