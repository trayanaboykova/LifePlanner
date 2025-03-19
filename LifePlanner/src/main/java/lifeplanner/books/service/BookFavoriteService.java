package lifeplanner.books.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookFavorite;
import lifeplanner.books.model.BookFavoriteId;
import lifeplanner.books.repository.BookFavoriteRepository;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.exception.DomainException;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookFavoriteService {
    private final BookFavoriteRepository bookFavoriteRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookFavoriteService(BookFavoriteRepository favoriteRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.bookFavoriteRepository = favoriteRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public long getFavoriteCount(UUID bookId) {
        return bookFavoriteRepository.countByBookId(bookId);
    }

    public boolean toggleFavorite(UUID bookId, UUID userId) {
        BookFavoriteId favoriteId = new BookFavoriteId(bookId, userId);
        if (bookFavoriteRepository.existsById(favoriteId)) {
            bookFavoriteRepository.deleteById(favoriteId);
            return false; // now unfavorited
        } else {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new DomainException("Book not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DomainException("User not found"));

            BookFavorite favorite = new BookFavorite();
            favorite.setId(favoriteId);
            favorite.setBook(book);
            favorite.setUser(user);
            bookFavoriteRepository.save(favorite);
            return true; // now favorited
        }
    }

    public List<Book> getFavoritesByUser(User user) {
        List<BookFavorite> favorites = bookFavoriteRepository.findAllByUser(user);
        return favorites.stream()
                .map(BookFavorite::getBook)
                .collect(Collectors.toList());
    }

    public void removeFavorite(User user, UUID bookId) {
        Optional<BookFavorite> favoriteOpt = bookFavoriteRepository.findByUserAndBookId(user, bookId);
        favoriteOpt.ifPresent(bookFavoriteRepository::delete);
    }
}