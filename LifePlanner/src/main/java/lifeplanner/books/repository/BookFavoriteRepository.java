package lifeplanner.books.repository;

import lifeplanner.books.model.BookFavorite;
import lifeplanner.books.model.BookFavoriteId;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookFavoriteRepository extends JpaRepository<BookFavorite, BookFavoriteId> {

    long countByBookId(UUID bookId);

    boolean existsById(BookFavoriteId id);

    List<BookFavorite> findAllByUser(User user);

    Optional<BookFavorite> findByUserAndBookId(User user, UUID bookId);

}