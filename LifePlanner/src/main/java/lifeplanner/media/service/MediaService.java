package lifeplanner.media.service;

import jakarta.validation.Valid;
import lifeplanner.exception.DomainException;
import lifeplanner.media.model.Media;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddMediaRequest;
import lifeplanner.web.dto.EditMediaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    @Autowired
    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> getMediaByUser(User user) {
        return mediaRepository.findAllByOwner(user);
    }

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
                .orElseThrow(() -> new DomainException("Media with id [" + mediaId + "] does not exist."));
    }

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

    public void shareMedia(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new DomainException("Media not found"));

        media.setVisible(true);
        mediaRepository.save(media);
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

//    public List<Media> getSharedMedia(User currentUser) {
//        return mediaRepository.findAllByVisibleTrue()
//                .stream()
//                .filter(media -> !media.getOwner().getId().equals(currentUser.getId()))
//                .toList();
//    }

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

    public void removeSharing(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new DomainException("Media not found"));
        media.setVisible(false);
        mediaRepository.save(media);
    }

    public void deleteMediaById(UUID id) {
        mediaRepository.deleteById(id);
    }

    public List<Media> getPendingMedia() {
        return mediaRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    public void approveMedia(UUID mediaId) {
        Media media = getMediaById(mediaId);
        media.setApprovalStatus(ApprovalStatus.APPROVED);
        mediaRepository.save(media);
    }

    public void rejectMedia(UUID mediaId) {
        Media media = getMediaById(mediaId);
        media.setApprovalStatus(ApprovalStatus.REJECTED);
        mediaRepository.save(media);
    }
}