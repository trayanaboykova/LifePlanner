package lifeplanner.books;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookRating;
import lifeplanner.books.model.BookStatus;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.books.service.BookLikesService;
import lifeplanner.books.service.BookService;
import lifeplanner.exception.DomainException;
import lifeplanner.exception.books.*;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddBookRequest;
import lifeplanner.web.dto.EditBookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BooksServiceUTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikesService bookLikesService;

    @Mock
    private BookFavoriteService bookFavoriteService;

    @InjectMocks
    private BookService bookService;

    @Test
    void givenNullUser_whenGetBooksByUser_thenThrowDomainException() {
        // When & Then
        DomainException exception = assertThrows(DomainException.class,
                () -> bookService.getBooksByUser(null));
        assertEquals("User must be provided", exception.getMessage());
        verifyNoInteractions(bookRepository);
    }

    @Test
    void givenValidUser_whenGetBooksByUser_thenReturnListOfBooks() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        List<Book> bookList = List.of(new Book(), new Book());
        when(bookRepository.findAllByOwner(user)).thenReturn(bookList);

        // When
        List<Book> result = bookService.getBooksByUser(user);

        // Then
        assertThat(result).hasSize(2);
        verify(bookRepository, times(1)).findAllByOwner(user);
    }

    @Test
    void givenAddBookRequest_whenAddBook_thenBookIsSaved() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());

        AddBookRequest request = new AddBookRequest();
        request.setTitle("Test Title");
        request.setAuthor("Test Author");
        request.setGenre("Fiction");

        // When
        bookService.addBook(request, user);

        // Then
        verify(bookRepository, times(1)).save(argThat(book ->
                "Test Title".equals(book.getTitle()) &&
                        "Test Author".equals(book.getAuthor()) &&
                        "Fiction".equals(book.getGenre()) &&
                        book.getOwner().equals(user) &&
                        !book.isVisible() &&
                        book.getApprovalStatus() == ApprovalStatus.PENDING
        ));
    }

    @Test
    void givenNonExistingBookId_whenEditBook_thenThrowBookNotFoundException() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.shareBook(bookId));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void givenExistingBook_whenEditBook_thenBookIsUpdated() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("Old Author");
        existingBook.setGenre("Fiction");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        EditBookRequest editRequest = EditBookRequest.builder()
                .title("New Title")
                .author("New Author")
                .genre("Non-fiction")
                .bookStatus(BookStatus.READ)
                .dateRead(LocalDate.now())
                .bookRating(BookRating.FIVE)
                .build();

        // When
        bookService.editBook(bookId, editRequest);

        // Then
        verify(bookRepository, times(1)).save(argThat(book ->
                "New Title".equals(book.getTitle()) &&
                        "New Author".equals(book.getAuthor()) &&
                        "Non-fiction".equals(book.getGenre())
        ));
    }

    @Test
    void givenNonExistingBookId_whenShareBook_thenThrowBookNotFoundException() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.shareBook(bookId));
    }

    @Test
    void givenApprovedAndNotVisibleBook_whenShareBook_thenSetVisibleTrueAndSave() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setVisible(false);
        book.setApprovalStatus(ApprovalStatus.APPROVED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.shareBook(bookId);

        // Then
        assertTrue(book.isVisible());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void givenVisibleBooks_whenGetMySharedBooks_thenReturnBooksOfCurrentUser() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(currentUserId);

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        Book book1 = new Book();
        book1.setOwner(currentUser);
        book1.setVisible(true);

        Book book2 = new Book();
        book2.setOwner(otherUser);
        book2.setVisible(true);

        when(bookRepository.findAllByVisibleTrue()).thenReturn(Arrays.asList(book1, book2));

        // When
        List<Book> mySharedBooks = bookService.getMySharedBooks(currentUser);

        // Then
        assertThat(mySharedBooks).containsExactly(book1);
    }

    @Test
    void givenApprovedSharedBooks_whenGetApprovedSharedBooks_thenExcludeCurrentUserBooks() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(currentUserId);

        User otherUser1 = new User();
        otherUser1.setId(UUID.randomUUID());

        User otherUser2 = new User();
        otherUser2.setId(UUID.randomUUID());

        Book book1 = new Book();
        book1.setOwner(otherUser1);
        book1.setVisible(true);
        book1.setApprovalStatus(ApprovalStatus.APPROVED);

        Book book2 = new Book();
        book2.setOwner(currentUser);
        book2.setVisible(true);
        book2.setApprovalStatus(ApprovalStatus.APPROVED);

        Book book3 = new Book();
        book3.setOwner(otherUser2);
        book3.setVisible(true);
        book3.setApprovalStatus(ApprovalStatus.APPROVED);

        when(bookRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED))
                .thenReturn(Arrays.asList(book1, book2, book3));

        // When
        List<Book> approvedSharedBooks = bookService.getApprovedSharedBooks(currentUser);

        // Then: Only book1 and book3 should be returned because they are not owned by the current user.
        assertThat(approvedSharedBooks).containsExactlyInAnyOrder(book1, book3);
    }

    @Test
    void givenNonExistingBookId_whenRemoveSharing_thenThrowBookNotFoundException() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.removeSharing(bookId));
    }

    @Test
    void givenInvisibleBook_whenRemoveSharing_thenThrowBookNotSharedException() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setVisible(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BookNotSharedException.class, () -> bookService.removeSharing(bookId));
    }

    @Test
    void givenVisibleBook_whenRemoveSharing_thenBookSetToInvisibleAndSaved() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setVisible(true);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.removeSharing(bookId);

        // Then
        assertFalse(book.isVisible());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void givenBookId_whenDeleteBookById_thenRepositoryDeleteIsCalled() {
        // Given
        UUID bookId = UUID.randomUUID();

        // When
        bookService.deleteBookById(bookId);

        // Then
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void givenBooksInDb_whenGetAllBooks_thenReturnList() {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of(new Book(), new Book()));

        // When
        List<Book> allBooks = bookService.getAllBooks();

        // Then
        assertThat(allBooks).hasSize(2);
    }

    @Test
    void givenPendingBooksInDb_whenGetPendingBooks_thenReturnPendingOnly() {
        // Given
        Book book1 = new Book();
        book1.setApprovalStatus(ApprovalStatus.PENDING);
        Book book2 = new Book();
        book2.setApprovalStatus(ApprovalStatus.PENDING);
        when(bookRepository.findAllByApprovalStatus(ApprovalStatus.PENDING))
                .thenReturn(List.of(book1, book2));

        // When
        List<Book> pendingBooks = bookService.getPendingBooks();

        // Then
        assertThat(pendingBooks).hasSize(2);
    }

    @Test
    void givenNonExistingBookId_whenApproveBook_thenThrowBookNotFoundException() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.approveBook(bookId));
    }

    @Test
    void givenBookIsRejected_whenApproveBook_thenUpdateApprovalStatusAndSave() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setApprovalStatus(ApprovalStatus.REJECTED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.approveBook(bookId);

        // Then
        assertEquals(ApprovalStatus.APPROVED, book.getApprovalStatus());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void givenBookIsAlreadyRejected_whenRejectBook_thenThrowBookAlreadyRejectedException() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setApprovalStatus(ApprovalStatus.REJECTED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BookAlreadyRejectedException.class, () -> bookService.rejectBook(bookId));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void givenBookNotRejected_whenRejectBook_thenSetApprovalStatusToRejectedAndSave() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setApprovalStatus(ApprovalStatus.APPROVED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.rejectBook(bookId);

        // Then
        assertEquals(ApprovalStatus.REJECTED, book.getApprovalStatus());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void givenBookAlreadyApproved_whenApproveBook_thenThrowBookAlreadyApprovedException() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setApprovalStatus(ApprovalStatus.APPROVED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BookAlreadyApprovedException.class, () -> bookService.approveBook(bookId));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void givenBooks_whenGetLikeCountsForBooks_thenReturnMappingOfBookIdToLikeCount() {
        // Given
        Book book1 = new Book();
        UUID id1 = UUID.randomUUID();
        book1.setId(id1);

        Book book2 = new Book();
        UUID id2 = UUID.randomUUID();
        book2.setId(id2);

        // Configure the mock to return specific like counts for each book id.
        when(bookLikesService.getLikeCount(id1)).thenReturn(3L);
        when(bookLikesService.getLikeCount(id2)).thenReturn(5L);

        // When
        Map<UUID, Long> likeCounts = bookService.getLikeCountsForBooks(Arrays.asList(book1, book2));

        // Then
        assertEquals(3L, likeCounts.get(id1));
        assertEquals(5L, likeCounts.get(id2));
        verify(bookLikesService).getLikeCount(id1);
        verify(bookLikesService).getLikeCount(id2);
    }

    @Test
    void givenBooks_whenGetFavoriteCountsForBooks_thenReturnMappingOfBookIdToFavoriteCount() {
        // Given
        Book book1 = new Book();
        UUID id1 = UUID.randomUUID();
        book1.setId(id1);

        Book book2 = new Book();
        UUID id2 = UUID.randomUUID();
        book2.setId(id2);

        // Configure the mock to return specific favorite counts for each book id.
        when(bookFavoriteService.getFavoriteCount(id1)).thenReturn(7L);
        when(bookFavoriteService.getFavoriteCount(id2)).thenReturn(2L);

        // When
        Map<UUID, Long> favoriteCounts = bookService.getFavoriteCountsForBooks(Arrays.asList(book1, book2));

        // Then
        assertEquals(7L, favoriteCounts.get(id1));
        assertEquals(2L, favoriteCounts.get(id2));
        verify(bookFavoriteService).getFavoriteCount(id1);
        verify(bookFavoriteService).getFavoriteCount(id2);
    }
}