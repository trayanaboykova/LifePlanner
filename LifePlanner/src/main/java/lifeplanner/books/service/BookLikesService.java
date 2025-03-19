package lifeplanner.books.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookLikes;
import lifeplanner.books.model.BookLikesId;
import lifeplanner.books.repository.BookLikesRepository;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.exception.DomainException;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookLikesService {

    private final BookLikesRepository bookLikesRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookLikesService(BookLikesRepository bookLikesRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.bookLikesRepository = bookLikesRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public long getLikeCount(UUID bookId) {
        return bookLikesRepository.countByBookId(bookId);
    }

    public boolean toggleLike(UUID bookId, UUID userId) {
        BookLikesId likeId = new BookLikesId(bookId, userId);

        // If already liked, remove the like
        if (bookLikesRepository.existsById(likeId)) {
            bookLikesRepository.deleteById(likeId);
            return false; // false means "now unliked"
        } else {
            // Otherwise, create a new like
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new DomainException("Book not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DomainException("User not found"));

            BookLikes newLike = new BookLikes();
            newLike.setId(likeId);
            newLike.setBook(book);
            newLike.setUser(user);
            bookLikesRepository.save(newLike);
            return true; // true means "now liked"
        }
    }
}