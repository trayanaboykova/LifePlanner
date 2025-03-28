package lifeplanner.books.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.exception.DomainException;
import lifeplanner.exception.books.*;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddBookRequest;
import lifeplanner.web.dto.EditBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookLikesService bookLikesService;
    private final BookFavoriteService bookFavoriteService;

    @Autowired
    public BookService(BookRepository bookRepository, BookLikesService bookLikesService, BookFavoriteService bookFavoriteService) {
        this.bookRepository = bookRepository;
        this.bookLikesService = bookLikesService;
        this.bookFavoriteService = bookFavoriteService;
    }

    public List<Book> getBooksByUser(User user) {
        if (user == null) {
            throw new DomainException("User must be provided");
        }
        return bookRepository.findAllByOwner(user);
    }

    @Transactional
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

    @Transactional
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
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    public Map<UUID, Long> getLikeCountsForBooks(List<Book> books) {
        return books.stream()
                .collect(Collectors.toMap(
                        Book::getId,
                        book -> bookLikesService.getLikeCount(book.getId())
                ));
    }

    public Map<UUID, Long> getFavoriteCountsForBooks(List<Book> books) {
        return books.stream()
                .collect(Collectors.toMap(
                        Book::getId,
                        book -> bookFavoriteService.getFavoriteCount(book.getId())
                ));
    }

    @Transactional
    public void shareBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        book.setVisible(true);
        bookRepository.save(book);
    }

    public List<Book> getMySharedBooks(User currentUser) {
        return bookRepository.findAllByVisibleTrue()
                .stream()
                .filter(book -> book.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Book> getApprovedSharedBooks(User currentUser) {
        List<Book> approvedBooks = bookRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED);
        return approvedBooks.stream()
                .filter(book -> !book.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    @Transactional
    public void removeSharing(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (!book.isVisible()) {
            throw new BookNotSharedException(bookId);
        }
        book.setVisible(false);
        bookRepository.save(book);
    }

    @Transactional
    public void deleteBookById(UUID id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getPendingBooks() {
        return bookRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Transactional
    public void approveBook(UUID bookId) {
        Book book = getBookById(bookId);

        if (book.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new BookAlreadyApprovedException(bookId);
        }

        book.setApprovalStatus(ApprovalStatus.APPROVED);
        bookRepository.save(book);
    }

    @Transactional
    public void rejectBook(UUID bookId) {
        Book book = getBookById(bookId);

        if (book.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new BookAlreadyRejectedException(bookId);
        }

        book.setApprovalStatus(ApprovalStatus.REJECTED);
        bookRepository.save(book);
    }
}