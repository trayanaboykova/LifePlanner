package lifeplanner.media;

import lifeplanner.exception.DomainException;
import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaLikesId;
import lifeplanner.media.repository.MediaLikesRepository;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.media.service.MediaLikesService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaLikesServiceUTest {

    @Mock
    private MediaLikesRepository mediaLikesRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MediaLikesService mediaLikesService;

    @Test
    void givenMediaId_whenGetLikeCount_thenReturnCount() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaLikesRepository.countByMediaId(mediaId)).thenReturn(5L);

        // When
        long count = mediaLikesService.getLikeCount(mediaId);

        // Then
        assertEquals(5L, count);
        verify(mediaLikesRepository, times(1)).countByMediaId(mediaId);
    }

    @Test
    void givenExistingLike_whenToggleLike_thenRemoveLikeAndReturnFalse() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MediaLikesId likeId = new MediaLikesId(mediaId, userId);
        when(mediaLikesRepository.existsById(likeId)).thenReturn(true);

        // When
        boolean result = mediaLikesService.toggleLike(mediaId, userId);

        // Then
        assertFalse(result);
        verify(mediaLikesRepository, times(1)).deleteById(likeId);
        verify(mediaLikesRepository, never()).save(any());
    }

    @Test
    void givenNoExistingLike_whenToggleLike_thenAddLikeAndReturnTrue() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MediaLikesId likeId = new MediaLikesId(mediaId, userId);

        Media media = new Media();
        User user = new User();

        when(mediaLikesRepository.existsById(likeId)).thenReturn(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = mediaLikesService.toggleLike(mediaId, userId);

        // Then
        assertTrue(result);
        verify(mediaLikesRepository, times(1)).save(argThat(like ->
                like.getId().equals(likeId) &&
                        like.getMedia().equals(media) &&
                        like.getUser().equals(user)
        ));
    }

    @Test
    void givenNonExistingMedia_whenToggleLike_thenThrowDomainException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(mediaLikesRepository.existsById(any())).thenReturn(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // When & Then
        DomainException exception = assertThrows(DomainException.class,
                () -> mediaLikesService.toggleLike(mediaId, userId));
        assertEquals("Media type not found", exception.getMessage());
    }

    @Test
    void givenNonExistingUser_whenToggleLike_thenThrowDomainException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Media media = new Media();

        when(mediaLikesRepository.existsById(any())).thenReturn(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        DomainException exception = assertThrows(DomainException.class,
                () -> mediaLikesService.toggleLike(mediaId, userId));
        assertEquals("User not found", exception.getMessage());
    }
}