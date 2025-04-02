package lifeplanner.books;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookLikesId;
import lifeplanner.books.repository.BookLikesRepository;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.books.service.BookLikesService;
import lifeplanner.exception.DomainException;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookLikesServiceUTest {

    @Mock
    private BookLikesRepository bookLikesRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookLikesService bookLikesService;

    @Test
    void getLikeCount_returnsCount() {
        // Given
        UUID bookId = UUID.randomUUID();
        when(bookLikesRepository.countByBookId(bookId)).thenReturn(5L);

        // When
        long count = bookLikesService.getLikeCount(bookId);

        // Then
        assertEquals(5L, count);
        verify(bookLikesRepository).countByBookId(bookId);
    }

    @Test
    void toggleLike_whenAlreadyLiked_thenDeleteAndReturnFalse() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookLikesId likeId = new BookLikesId(bookId, userId);
        when(bookLikesRepository.existsById(likeId)).thenReturn(true);

        // When
        boolean result = bookLikesService.toggleLike(bookId, userId);

        // Then
        assertFalse(result, "Expected toggleLike to return false when like is removed");
        verify(bookLikesRepository).deleteById(likeId);
    }

    @Test
    void toggleLike_whenNotLiked_thenCreateAndReturnTrue() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookLikesId likeId = new BookLikesId(bookId, userId);
        when(bookLikesRepository.existsById(likeId)).thenReturn(false);

        // Book and user must exist for creating a like
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = bookLikesService.toggleLike(bookId, userId);

        // Then
        assertTrue(result, "Expected toggleLike to return true when a like is created");
        verify(bookLikesRepository).save(argThat(newLike ->
                likeId.equals(newLike.getId()) &&
                        book == newLike.getBook() &&
                        user == newLike.getUser()
        ));
    }

    @Test
    void toggleLike_whenBookNotFound_thenThrowException() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookLikesId likeId = new BookLikesId(bookId, userId);
        when(bookLikesRepository.existsById(likeId)).thenReturn(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        DomainException ex = assertThrows(DomainException.class, () ->
                bookLikesService.toggleLike(bookId, userId));
        assertEquals("Book not found", ex.getMessage());
        verify(bookRepository).findById(bookId);
        verifyNoInteractions(userRepository);
    }

    @Test
    void toggleLike_whenUserNotFound_thenThrowException() {
        // Given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookLikesId likeId = new BookLikesId(bookId, userId);
        when(bookLikesRepository.existsById(likeId)).thenReturn(false);
        // Book exists...
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        // But user does not exist
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        DomainException ex = assertThrows(DomainException.class, () ->
                bookLikesService.toggleLike(bookId, userId));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(userId);
    }
}