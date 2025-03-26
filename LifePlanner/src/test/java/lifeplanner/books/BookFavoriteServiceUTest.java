package lifeplanner.books;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookFavorite;
import lifeplanner.books.model.BookFavoriteId;
import lifeplanner.books.repository.BookFavoriteRepository;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.exception.DomainException;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookFavoriteServiceUTest {
    @Mock
    private BookFavoriteRepository bookFavoriteRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookFavoriteService bookFavoriteService;

    @Test
    void getFavoriteCount_returnsCount() {
        UUID bookId = UUID.randomUUID();
        when(bookFavoriteRepository.countByBookId(bookId)).thenReturn(3L);

        long count = bookFavoriteService.getFavoriteCount(bookId);

        assertEquals(3L, count);
        verify(bookFavoriteRepository).countByBookId(bookId);
    }

    @Test
    void toggleFavorite_whenAlreadyFavorited_thenDeleteAndReturnFalse() {
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookFavoriteId favoriteId = new BookFavoriteId(bookId, userId);
        when(bookFavoriteRepository.existsById(favoriteId)).thenReturn(true);

        boolean result = bookFavoriteService.toggleFavorite(bookId, userId);

        assertFalse(result, "Expected toggleFavorite to return false when favorite is removed");
        verify(bookFavoriteRepository).deleteById(favoriteId);
    }

    @Test
    void toggleFavorite_whenNotFavorited_thenCreateFavoriteAndReturnTrue() {
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookFavoriteId favoriteId = new BookFavoriteId(bookId, userId);
        when(bookFavoriteRepository.existsById(favoriteId)).thenReturn(false);

        // Setup: book and user must exist
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = bookFavoriteService.toggleFavorite(bookId, userId);

        assertTrue(result, "Expected toggleFavorite to return true when favorite is created");
        verify(bookFavoriteRepository).save(argThat(favorite ->
                favoriteId.equals(favorite.getId()) &&
                        book == favorite.getBook() &&
                        user == favorite.getUser()
        ));
    }

    @Test
    void toggleFavorite_whenBookNotFound_thenThrowException() {
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookFavoriteId favoriteId = new BookFavoriteId(bookId, userId);
        when(bookFavoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () ->
                bookFavoriteService.toggleFavorite(bookId, userId));
        assertEquals("Book not found", ex.getMessage());
        verify(bookRepository).findById(bookId);
        verifyNoInteractions(userRepository);
    }

    @Test
    void toggleFavorite_whenUserNotFound_thenThrowException() {
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookFavoriteId favoriteId = new BookFavoriteId(bookId, userId);
        when(bookFavoriteRepository.existsById(favoriteId)).thenReturn(false);
        // Book exists...
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        // But user is missing:
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () ->
                bookFavoriteService.toggleFavorite(bookId, userId));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getFavoritesByUser_returnsMappedBooks() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Book book1 = new Book();
        Book book2 = new Book();
        BookFavorite favorite1 = new BookFavorite();
        favorite1.setBook(book1);
        BookFavorite favorite2 = new BookFavorite();
        favorite2.setBook(book2);

        when(bookFavoriteRepository.findAllByUser(user)).thenReturn(Arrays.asList(favorite1, favorite2));

        List<Book> favorites = bookFavoriteService.getFavoritesByUser(user);

        assertThat(favorites).containsExactly(book1, book2);
        verify(bookFavoriteRepository).findAllByUser(user);
    }

    @Test
    void removeFavorite_whenFavoriteExists_thenDeleteFavorite() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID bookId = UUID.randomUUID();

        BookFavorite favorite = new BookFavorite();
        when(bookFavoriteRepository.findByUserAndBookId(user, bookId)).thenReturn(Optional.of(favorite));

        bookFavoriteService.removeFavorite(user, bookId);

        verify(bookFavoriteRepository).delete(favorite);
    }

    @Test
    void removeFavorite_whenFavoriteDoesNotExist_thenDoNothing() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID bookId = UUID.randomUUID();

        when(bookFavoriteRepository.findByUserAndBookId(user, bookId)).thenReturn(Optional.empty());

        // No exception should be thrown; simply nothing happens.
        bookFavoriteService.removeFavorite(user, bookId);

        verify(bookFavoriteRepository, never()).delete(any());
    }
}