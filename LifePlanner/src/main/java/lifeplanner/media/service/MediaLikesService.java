package lifeplanner.media.service;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookLikes;
import lifeplanner.books.model.BookLikesId;
import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaLikes;
import lifeplanner.media.model.MediaLikesId;
import lifeplanner.media.repository.MediaLikesRepository;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MediaLikesService {

    private final MediaLikesRepository mediaLikesRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    @Autowired
    public MediaLikesService(MediaLikesRepository mediaLikesRepository, MediaRepository mediaRepository, UserRepository userRepository) {
        this.mediaLikesRepository = mediaLikesRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public long getLikeCount(UUID mediaId) {
        return mediaLikesRepository.countByMediaId(mediaId);
    }


    public boolean toggleLike(UUID mediaId, UUID userId) {
        MediaLikesId likeId = new MediaLikesId(mediaId, userId);

        // If already liked, remove the like
        if (mediaLikesRepository.existsById(likeId)) {
            mediaLikesRepository.deleteById(likeId);
            return false; // false means "now unliked"
        } else {
            // Otherwise, create a new like
            Media media = mediaRepository.findById(mediaId)
                    .orElseThrow(() -> new RuntimeException("Media type not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            MediaLikes newLike = new MediaLikes();
            newLike.setId(likeId);
            newLike.setMedia(media);
            newLike.setUser(user);
            mediaLikesRepository.save(newLike);
            return true; // true means "now liked"
        }
    }
}
