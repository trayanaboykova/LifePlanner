package lifeplanner.books.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.user.model.ApprovalStatus;
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

    public List<Book> getBooksByUser(User user) {
        return bookRepository.findAllByOwner(user);
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
                .approvalStatus(ApprovalStatus.PENDING)
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

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(UUID bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book with id [" + bookId + "] does not exist."));
    }

    public void shareBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setVisible(true);
        bookRepository.save(book);
    }

    public List<Book> getSharedBooks(User currentUser) {
        return bookRepository.findAllByVisibleTrue()
                .stream()
                .filter(book -> !book.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Book> getMySharedBooks(User currentUser) {
        return bookRepository.findAllByVisibleTrue()
                .stream()
                .filter(book -> book.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public void removeSharing(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setVisible(false);
        bookRepository.save(book);
    }

    public void deleteBookById(UUID id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getPendingBooks() {
        return bookRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    public void approveBook(UUID bookId) {
        Book book = getBookById(bookId);
        book.setApprovalStatus(ApprovalStatus.APPROVED);
        bookRepository.save(book);
    }

    public void rejectBook(UUID bookId) {
        Book book = getBookById(bookId);
        book.setApprovalStatus(ApprovalStatus.REJECTED);
        bookRepository.save(book);
    }
}