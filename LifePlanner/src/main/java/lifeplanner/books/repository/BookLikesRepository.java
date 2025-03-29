package lifeplanner.books.repository;

import lifeplanner.books.model.BookLikes;
import lifeplanner.books.model.BookLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookLikesRepository extends JpaRepository<BookLikes, BookLikesId> {

    long countByBookId(UUID bookId);

    boolean existsById(BookLikesId id);

}