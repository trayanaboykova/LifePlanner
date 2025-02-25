package lifeplanner.books.repository;

import lifeplanner.books.model.BookLikes;
import lifeplanner.books.model.BookLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookLikesRepository extends JpaRepository<BookLikes, BookLikesId> {
    // Count how many likes a book has
    long countByBookId(UUID bookId);

    // Check if a user has already liked a book
    boolean existsById(BookLikesId id);

    // Optional: find all likes for a given book
    List<BookLikes> findAllByBookId(UUID bookId);
}
