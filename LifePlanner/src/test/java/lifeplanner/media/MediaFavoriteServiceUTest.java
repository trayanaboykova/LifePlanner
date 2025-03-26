package lifeplanner.media;

import lifeplanner.exception.DomainException;
import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaFavorite;
import lifeplanner.media.model.MediaFavoriteId;
import lifeplanner.media.repository.MediaFavoriteRepository;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaFavoriteServiceUTest {

    @Mock
    private MediaFavoriteRepository mediaFavoriteRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MediaFavoriteService mediaFavoriteService;

    @Test
    void givenMediaId_whenGetFavoriteCount_thenReturnCount() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaFavoriteRepository.countByMediaId(mediaId)).thenReturn(3L);

        // When
        long count = mediaFavoriteService.getFavoriteCount(mediaId);

        // Then
        assertEquals(3L, count);
        verify(mediaFavoriteRepository, times(1)).countByMediaId(mediaId);
    }

    @Test
    void givenExistingFavorite_whenToggleFavoriteMedia_thenRemoveFavoriteAndReturnFalse() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MediaFavoriteId favoriteId = new MediaFavoriteId(mediaId, userId);
        when(mediaFavoriteRepository.existsById(favoriteId)).thenReturn(true);

        // When
        boolean result = mediaFavoriteService.toggleFavoriteMedia(mediaId, userId);

        // Then
        assertFalse(result);
        verify(mediaFavoriteRepository, times(1)).deleteById(favoriteId);
        verify(mediaFavoriteRepository, never()).save(any());
    }

    @Test
    void givenNoExistingFavorite_whenToggleFavoriteMedia_thenAddFavoriteAndReturnTrue() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MediaFavoriteId favoriteId = new MediaFavoriteId(mediaId, userId);

        Media media = new Media();
        User user = new User();

        when(mediaFavoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = mediaFavoriteService.toggleFavoriteMedia(mediaId, userId);

        // Then
        assertTrue(result);
        verify(mediaFavoriteRepository, times(1)).save(argThat(fav ->
                fav.getId().equals(favoriteId) &&
                        fav.getMedia().equals(media) &&
                        fav.getUser().equals(user)
        ));
    }

    @Test
    void givenNonExistingMedia_whenToggleFavoriteMedia_thenThrowDomainException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(mediaFavoriteRepository.existsById(any())).thenReturn(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // When & Then
        DomainException exception = assertThrows(DomainException.class,
                () -> mediaFavoriteService.toggleFavoriteMedia(mediaId, userId));
        assertEquals("Media not found", exception.getMessage());
    }

    @Test
    void givenNonExistingUser_whenToggleFavoriteMedia_thenThrowDomainException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Media media = new Media();

        when(mediaFavoriteRepository.existsById(any())).thenReturn(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        DomainException exception = assertThrows(DomainException.class,
                () -> mediaFavoriteService.toggleFavoriteMedia(mediaId, userId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void givenUser_whenGetFavoritesByUser_thenReturnFavoriteMediaList() {
        // Given
        User user = new User();
        Media media1 = new Media();
        Media media2 = new Media();

        MediaFavorite fav1 = new MediaFavorite();
        fav1.setMedia(media1);
        MediaFavorite fav2 = new MediaFavorite();
        fav2.setMedia(media2);

        when(mediaFavoriteRepository.findAllByUser(user)).thenReturn(List.of(fav1, fav2));

        // When
        List<Media> favorites = mediaFavoriteService.getFavoritesByUser(user);

        // Then
        assertEquals(2, favorites.size());
        assertTrue(favorites.contains(media1));
        assertTrue(favorites.contains(media2));
    }

    @Test
    void givenUserWithNoFavorites_whenGetFavoritesByUser_thenReturnEmptyList() {
        // Given
        User user = new User();
        when(mediaFavoriteRepository.findAllByUser(user)).thenReturn(Collections.emptyList());

        // When
        List<Media> favorites = mediaFavoriteService.getFavoritesByUser(user);

        // Then
        assertTrue(favorites.isEmpty());
    }

    @Test
    void givenExistingFavorite_whenRemoveFavorite_thenDeleteFavorite() {
        // Given
        User user = new User();
        UUID mediaId = UUID.randomUUID();
        MediaFavorite favorite = new MediaFavorite();
        when(mediaFavoriteRepository.findByUserAndMediaId(user, mediaId))
                .thenReturn(Optional.of(favorite));

        // When
        mediaFavoriteService.removeFavorite(user, mediaId);

        // Then
        verify(mediaFavoriteRepository, times(1)).delete(favorite);
    }

    @Test
    void givenNonExistingFavorite_whenRemoveFavorite_thenDoNothing() {
        // Given
        User user = new User();
        UUID mediaId = UUID.randomUUID();
        when(mediaFavoriteRepository.findByUserAndMediaId(user, mediaId))
                .thenReturn(Optional.empty());

        // When
        mediaFavoriteService.removeFavorite(user, mediaId);

        // Then
        verify(mediaFavoriteRepository, never()).delete(any());
    }
}