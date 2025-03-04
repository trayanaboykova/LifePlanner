package lifeplanner.books.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookFavorite;
import lifeplanner.books.model.BookFavoriteId;
import lifeplanner.books.repository.BookFavoriteRepository;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookFavoriteService {
    private final BookFavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookFavoriteService(BookFavoriteRepository favoriteRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public long getFavoriteCount(UUID bookId) {
        return favoriteRepository.countByBookId(bookId);
    }

    public boolean toggleFavorite(UUID bookId, UUID userId) {
        BookFavoriteId favoriteId = new BookFavoriteId(bookId, userId);
        if (favoriteRepository.existsById(favoriteId)) {
            favoriteRepository.deleteById(favoriteId);
            return false; // now unfavorited
        } else {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BookFavorite favorite = new BookFavorite();
            favorite.setId(favoriteId);
            favorite.setBook(book);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
            return true; // now favorited
        }
    }

    public List<Book> getFavoritesByUser(User user) {
        List<BookFavorite> favorites = favoriteRepository.findAllByUser(user);
        return favorites.stream()
                .map(BookFavorite::getBook)
                .collect(Collectors.toList());
    }
}
