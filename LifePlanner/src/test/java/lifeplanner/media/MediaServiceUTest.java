package lifeplanner.media;

import lifeplanner.exception.DomainException;
import lifeplanner.exception.media.*;
import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaRating;
import lifeplanner.media.model.MediaStatus;
import lifeplanner.media.model.TypeMedia;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.media.service.MediaLikesService;
import lifeplanner.media.service.MediaService;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddMediaRequest;
import lifeplanner.web.dto.EditMediaRequest;
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
public class MediaServiceUTest {
    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private MediaLikesService mediaLikesService;

    @Mock
    private MediaFavoriteService mediaFavoriteService;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void givenNullUser_whenGetMediaByUser_thenThrowDomainException() {
        // When & Then
        DomainException exception = assertThrows(DomainException.class,
                () -> mediaService.getMediaByUser(null));
        assertEquals("User must be provided", exception.getMessage());
        verifyNoInteractions(mediaRepository);
    }

    @Test
    void givenValidUser_whenGetMediaByUser_thenReturnListOfMedia() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        List<Media> mediaList = List.of(new Media(), new Media());
        when(mediaRepository.findAllByOwner(user)).thenReturn(mediaList);

        // When
        List<Media> result = mediaService.getMediaByUser(user);

        // Then
        assertThat(result).hasSize(2);
        verify(mediaRepository, times(1)).findAllByOwner(user);
    }

    @Test
    void givenAddMediaRequest_whenAddMedia_thenMediaIsSaved() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());

        AddMediaRequest request = new AddMediaRequest();
        request.setTitle("Test Title");
        request.setType(TypeMedia.MOVIE);
        request.setStatus(MediaStatus.WATCHED);
        request.setRating(MediaRating.FIVE);
        request.setDateRated(LocalDate.now());
        request.setGenre("Action");

        // When
        mediaService.addMedia(request, user);

        // Then
        verify(mediaRepository, times(1)).save(argThat(media ->
                "Test Title".equals(media.getTitle()) &&
                        TypeMedia.MOVIE == media.getType() &&
                        MediaStatus.WATCHED == media.getStatus() &&
                        MediaRating.FIVE == media.getRating() &&
                        "Action".equals(media.getGenre()) &&
                        media.getOwner().equals(user) &&
                        !media.isVisible() &&
                        media.getApprovalStatus() == ApprovalStatus.PENDING
        ));
    }

    @Test
    void givenNonExistingMediaId_whenEditMedia_thenThrowMediaNotFoundException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MediaNotFoundException.class, () -> mediaService.shareMedia(mediaId));
        verify(mediaRepository, never()).save(any());
    }

    @Test
    void givenExistingMedia_whenEditMedia_thenMediaIsUpdated() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media existingMedia = new Media();
        existingMedia.setId(mediaId);
        existingMedia.setTitle("Old Title");
        existingMedia.setType(TypeMedia.MOVIE);
        existingMedia.setStatus(MediaStatus.WATCHED);
        existingMedia.setRating(MediaRating.THREE);
        existingMedia.setGenre("Action");

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(existingMedia));

        EditMediaRequest editRequest = EditMediaRequest.builder()
                .title("New Title")
                .type(TypeMedia.TV_SHOW)
                .status(MediaStatus.WATCHED)
                .rating(MediaRating.FIVE)
                .dateRated(LocalDate.now())
                .genre("Drama")
                .build();

        // When
        mediaService.editMedia(mediaId, editRequest);

        // Then
        verify(mediaRepository, times(1)).save(argThat(media ->
                "New Title".equals(media.getTitle()) &&
                        TypeMedia.TV_SHOW == media.getType() &&
                        MediaStatus.WATCHED == media.getStatus() &&
                        MediaRating.FIVE == media.getRating() &&
                        "Drama".equals(media.getGenre())
        ));
    }

    @Test
    void givenNonExistingMediaId_whenShareMedia_thenThrowMediaNotFoundException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MediaNotFoundException.class, () -> mediaService.shareMedia(mediaId));
    }

//    @Test
//    void givenAlreadyVisibleMedia_whenShareMedia_thenThrowMediaAlreadySharedException() {
//        // Given
//        UUID mediaId = UUID.randomUUID();
//        Media media = new Media();
//        media.setId(mediaId);
//        media.setVisible(true);
//        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
//
//        // When & Then
//        assertThrows(MediaAlreadySharedException.class, () -> mediaService.shareMedia(mediaId));
//    }

//    @Test
//    void givenRejectedMedia_whenShareMedia_thenThrowMediaRejectedException() {
//        // Given
//        UUID mediaId = UUID.randomUUID();
//        Media media = new Media();
//        media.setId(mediaId);
//        media.setApprovalStatus(ApprovalStatus.REJECTED);
//        media.setVisible(false);
//        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
//
//        // When & Then
//        assertThrows(MediaRejectedException.class, () -> mediaService.shareMedia(mediaId));
//    }

//    @Test
//    void givenPendingMedia_whenShareMedia_thenThrowMediaPendingApprovalException() {
//        // Given
//        UUID mediaId = UUID.randomUUID();
//        Media media = new Media();
//        media.setId(mediaId);
//        media.setApprovalStatus(ApprovalStatus.PENDING);
//        media.setVisible(false);
//        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
//
//        // When & Then
//        assertThrows(MediaPendingApprovalException.class, () -> mediaService.shareMedia(mediaId));
//    }

    @Test
    void givenApprovedAndNotVisibleMedia_whenShareMedia_thenSetVisibleTrueAndSave() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setVisible(false);
        media.setApprovalStatus(ApprovalStatus.APPROVED);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When
        mediaService.shareMedia(mediaId);

        // Then
        assertTrue(media.isVisible());
        verify(mediaRepository, times(1)).save(media);
    }

    @Test
    void givenVisibleMedia_whenGetMySharedMedia_thenReturnMediaOfCurrentUser() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(currentUserId);

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        Media media1 = new Media();
        media1.setOwner(currentUser);
        media1.setVisible(true);

        Media media2 = new Media();
        media2.setOwner(otherUser);
        media2.setVisible(true);

        when(mediaRepository.findAllByVisibleTrue()).thenReturn(Arrays.asList(media1, media2));

        // When
        List<Media> mySharedMedia = mediaService.getMySharedMedia(currentUser);

        // Then
        assertThat(mySharedMedia).containsExactly(media1);
    }

    @Test
    void givenApprovedSharedMedia_whenGetApprovedSharedMedia_thenExcludeCurrentUserMedia() {
        // Given
        UUID currentUserId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(currentUserId);

        User otherUser1 = new User();
        otherUser1.setId(UUID.randomUUID());

        User otherUser2 = new User();
        otherUser2.setId(UUID.randomUUID());

        Media media1 = new Media();
        media1.setOwner(otherUser1);
        media1.setVisible(true);
        media1.setApprovalStatus(ApprovalStatus.APPROVED);

        Media media2 = new Media();
        media2.setOwner(currentUser);
        media2.setVisible(true);
        media2.setApprovalStatus(ApprovalStatus.APPROVED);

        Media media3 = new Media();
        media3.setOwner(otherUser2);
        media3.setVisible(true);
        media3.setApprovalStatus(ApprovalStatus.APPROVED);

        when(mediaRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED))
                .thenReturn(Arrays.asList(media1, media2, media3));

        // When
        List<Media> approvedSharedMedia = mediaService.getApprovedSharedMedia(currentUser);

        // Then: Only media1 and media3 should be returned because they are not owned by the current user.
        assertThat(approvedSharedMedia).containsExactlyInAnyOrder(media1, media3);
    }

    @Test
    void givenNonExistingMediaId_whenRemoveSharing_thenThrowMediaNotFoundException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MediaNotFoundException.class, () -> mediaService.removeSharing(mediaId));
    }

    @Test
    void givenInvisibleMedia_whenRemoveSharing_thenThrowMediaNotSharedException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setVisible(false);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When & Then
        assertThrows(MediaNotSharedException.class, () -> mediaService.removeSharing(mediaId));
    }

    @Test
    void givenVisibleMedia_whenRemoveSharing_thenMediaSetToInvisibleAndSaved() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setVisible(true);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When
        mediaService.removeSharing(mediaId);

        // Then
        assertFalse(media.isVisible());
        verify(mediaRepository, times(1)).save(media);
    }

    @Test
    void givenMediaId_whenDeleteMediaById_thenRepositoryDeleteIsCalled() {
        // Given
        UUID mediaId = UUID.randomUUID();

        // When
        mediaService.deleteMediaById(mediaId);

        // Then
        verify(mediaRepository, times(1)).deleteById(mediaId);
    }

    @Test
    void givenMediaInDb_whenGetAllMedia_thenReturnList() {
        // Given
        when(mediaRepository.findAll()).thenReturn(List.of(new Media(), new Media()));

        // When
        List<Media> allMedia = mediaService.getAllMedia();

        // Then
        assertThat(allMedia).hasSize(2);
    }

    @Test
    void givenPendingMediaInDb_whenGetPendingMedia_thenReturnPendingOnly() {
        // Given
        Media media1 = new Media();
        media1.setApprovalStatus(ApprovalStatus.PENDING);
        Media media2 = new Media();
        media2.setApprovalStatus(ApprovalStatus.PENDING);
        when(mediaRepository.findAllByApprovalStatus(ApprovalStatus.PENDING))
                .thenReturn(List.of(media1, media2));

        // When
        List<Media> pendingMedia = mediaService.getPendingMedia();

        // Then
        assertThat(pendingMedia).hasSize(2);
    }

    @Test
    void givenNonExistingMediaId_whenApproveMedia_thenThrowMediaNotFoundException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MediaNotFoundException.class, () -> mediaService.approveMedia(mediaId));
    }

    @Test
    void givenMediaIsRejected_whenApproveMedia_thenUpdateApprovalStatusAndSave() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setApprovalStatus(ApprovalStatus.REJECTED);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When
        mediaService.approveMedia(mediaId);

        // Then
        assertEquals(ApprovalStatus.APPROVED, media.getApprovalStatus());
        verify(mediaRepository, times(1)).save(media);
    }

    @Test
    void givenMediaIsAlreadyRejected_whenRejectMedia_thenThrowMediaAlreadyRejectedException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setApprovalStatus(ApprovalStatus.REJECTED);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When & Then
        assertThrows(MediaAlreadyRejectedException.class, () -> mediaService.rejectMedia(mediaId));
        verify(mediaRepository, never()).save(any(Media.class));
    }

    @Test
    void givenMediaNotRejected_whenRejectMedia_thenSetApprovalStatusToRejectedAndSave() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setApprovalStatus(ApprovalStatus.APPROVED);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When
        mediaService.rejectMedia(mediaId);

        // Then
        assertEquals(ApprovalStatus.REJECTED, media.getApprovalStatus());
        verify(mediaRepository, times(1)).save(media);
    }

    @Test
    void givenMediaAlreadyApproved_whenApproveMedia_thenThrowMediaAlreadyApprovedException() {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setApprovalStatus(ApprovalStatus.APPROVED);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        // When & Then
        assertThrows(MediaAlreadyApprovedException.class, () -> mediaService.approveMedia(mediaId));
        verify(mediaRepository, never()).save(any(Media.class));
    }

    @Test
    void givenMedia_whenGetLikeCountsForMedia_thenReturnMappingOfMediaIdToLikeCount() {
        // Given
        Media media1 = new Media();
        UUID id1 = UUID.randomUUID();
        media1.setId(id1);

        Media media2 = new Media();
        UUID id2 = UUID.randomUUID();
        media2.setId(id2);

        // Configure the mock to return specific like counts for each media id.
        when(mediaLikesService.getLikeCount(id1)).thenReturn(3L);
        when(mediaLikesService.getLikeCount(id2)).thenReturn(5L);

        // When
        Map<UUID, Long> likeCounts = mediaService.getLikeCountsForMedia(Arrays.asList(media1, media2));

        // Then
        assertEquals(3L, likeCounts.get(id1));
        assertEquals(5L, likeCounts.get(id2));
        verify(mediaLikesService).getLikeCount(id1);
        verify(mediaLikesService).getLikeCount(id2);
    }

    @Test
    void givenMedia_whenGetFavoriteCountsForMedia_thenReturnMappingOfMediaIdToFavoriteCount() {
        // Given
        Media media1 = new Media();
        UUID id1 = UUID.randomUUID();
        media1.setId(id1);

        Media media2 = new Media();
        UUID id2 = UUID.randomUUID();
        media2.setId(id2);

        // Configure the mock to return specific favorite counts for each media id.
        when(mediaFavoriteService.getFavoriteCount(id1)).thenReturn(7L);
        when(mediaFavoriteService.getFavoriteCount(id2)).thenReturn(2L);

        // When
        Map<UUID, Long> favoriteCounts = mediaService.getFavoriteCountsForMedia(Arrays.asList(media1, media2));

        // Then
        assertEquals(7L, favoriteCounts.get(id1));
        assertEquals(2L, favoriteCounts.get(id2));
        verify(mediaFavoriteService).getFavoriteCount(id1);
        verify(mediaFavoriteService).getFavoriteCount(id2);
    }
}