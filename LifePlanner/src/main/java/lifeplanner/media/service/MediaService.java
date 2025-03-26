package lifeplanner.media.service;

import jakarta.validation.Valid;
import lifeplanner.exception.media.*;
import lifeplanner.media.model.Media;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddMediaRequest;
import lifeplanner.web.dto.EditMediaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MediaLikesService mediaLikesService;
    private final MediaFavoriteService mediaFavoriteService;

    @Autowired
    public MediaService(MediaRepository mediaRepository, MediaLikesService mediaLikesService, MediaFavoriteService mediaFavoriteService) {
        this.mediaRepository = mediaRepository;
        this.mediaLikesService = mediaLikesService;
        this.mediaFavoriteService = mediaFavoriteService;
    }

    public List<Media> getMediaByUser(User user) {
        return mediaRepository.findAllByOwner(user);
    }

    @Transactional
    public void addMedia(@Valid AddMediaRequest addMediaRequest, User user) {
        Media media = Media.builder()
                .status(addMediaRequest.getStatus())
                .type(addMediaRequest.getType())
                .title(addMediaRequest.getTitle())
                .rating(addMediaRequest.getRating())
                .dateRated(addMediaRequest.getDateRated())
                .genre(addMediaRequest.getGenre())
                .owner(user)
                .visible(false)
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        mediaRepository.save(media);
    }

    public Media getMediaById(UUID mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));
    }

    @Transactional
    public void editMedia(UUID id, @Valid EditMediaRequest editMediaRequest) {
        Media media = getMediaById(id);
        media.setStatus(editMediaRequest.getStatus());
        media.setType(editMediaRequest.getType());
        media.setTitle(editMediaRequest.getTitle());
        media.setRating(editMediaRequest.getRating());
        media.setDateRated(editMediaRequest.getDateRated());
        media.setGenre(editMediaRequest.getGenre());

        mediaRepository.save(media);
    }

    @Transactional
    public void shareMedia(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));

        if (media.isVisible()) {
            throw new MediaAlreadySharedException(mediaId);
        }

        if (media.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new MediaRejectedException(mediaId);
        }

        if (media.getApprovalStatus() == ApprovalStatus.PENDING) {
            throw new MediaPendingApprovalException(mediaId);
        }

        media.setVisible(true);
        mediaRepository.save(media);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public Map<UUID, Long> getLikeCountsForMedia(List<Media> mediaList) {
        return mediaList.stream()
                .collect(Collectors.toMap(
                        Media::getId,
                        media -> mediaLikesService.getLikeCount(media.getId())
                ));
    }

    public Map<UUID, Long> getFavoriteCountsForMedia(List<Media> mediaList) {
        return mediaList.stream()
                .collect(Collectors.toMap(
                        Media::getId,
                        media -> mediaFavoriteService.getFavoriteCount(media.getId())
                ));
    }

    public List<Media> getApprovedSharedMedia(User currentUser) {
        List<Media> approvedMedia = mediaRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED);
        return approvedMedia
                .stream()
                .filter(media -> !media.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Media> getMySharedMedia(User currentUser) {
        return mediaRepository.findAllByVisibleTrue()
                .stream()
                .filter(media -> media.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    @Transactional
    public void removeSharing(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));
        media.setVisible(false);
        mediaRepository.save(media);
    }

    @Transactional
    public void deleteMediaById(UUID id) {
        mediaRepository.deleteById(id);
    }

    public List<Media> getPendingMedia() {
        return mediaRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Transactional
    public void approveMedia(UUID mediaId) {
        Media media = getMediaById(mediaId);
        if (media.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new MediaAlreadyApprovedException(mediaId);
        }
        media.setApprovalStatus(ApprovalStatus.APPROVED);
        mediaRepository.save(media);
    }

    @Transactional
    public void rejectMedia(UUID mediaId) {
        Media media = getMediaById(mediaId);
        if (media.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new MediaAlreadyRejectedException(mediaId);
        }
        media.setApprovalStatus(ApprovalStatus.REJECTED);
        mediaRepository.save(media);
    }
}