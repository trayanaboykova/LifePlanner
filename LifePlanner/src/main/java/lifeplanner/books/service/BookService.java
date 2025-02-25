package lifeplanner.books.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddBookRequest;
import lifeplanner.web.dto.EditBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(AddBookRequest addBookRequest, User user) {
        Book book = Book.builder()
                .bookStatus(addBookRequest.getBookStatus())
                .title(addBookRequest.getTitle())
                .author(addBookRequest.getAuthor())
                .dateRead(addBookRequest.getDateRead())
                .bookRating(addBookRequest.getBookRating())
                .genre(addBookRequest.getGenre())
                .owner(user)
                .visible(false)
                .build();

        bookRepository.save(book);
    }

    public void editBook(UUID bookId, EditBookRequest editBookRequest) {
        Book book = getBookById(bookId);
        book.setTitle(editBookRequest.getTitle());
        book.setAuthor(editBookRequest.getAuthor());
        book.setGenre(editBookRequest.getGenre());
        book.setDateRead(editBookRequest.getDateRead());
        book.setBookRating(editBookRequest.getBookRating());
        book.setBookStatus(editBookRequest.getBookStatus());

        bookRepository.save(book);
    }

    public List<Book> getBooksByUser(User user) {
        return bookRepository.findAllByOwner(user);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getSharedBooks() {
        return bookRepository.findAllByVisibleTrue();
    }

    public Book getBookById(UUID bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book with id [" + bookId + "] does not exist."));
    }

    public void deleteBookById(UUID id) {
        bookRepository.deleteById(id);
    }
}
