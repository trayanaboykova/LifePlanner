package lifeplanner.books.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class BookLikesId implements Serializable {

    private UUID bookId;

    private UUID userId;

    public BookLikesId(UUID bookId, UUID userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    public BookLikesId() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookLikesId that = (BookLikesId) o;
        return Objects.equals(bookId, that.bookId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, userId);
    }
}
