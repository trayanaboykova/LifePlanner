package lifeplanner.books.repository;

import lifeplanner.books.model.Book;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findAllByOwner(User user);

    List<Book> findAllByVisibleTrue();

    List<Book> findAllByApprovalStatus(ApprovalStatus pending);

}