package lifeplanner.media.service;

import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaFavorite;
import lifeplanner.media.model.MediaFavoriteId;
import lifeplanner.media.repository.MediaFavoriteRepository;
import lifeplanner.media.repository.MediaRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MediaFavoriteService {
    private final MediaFavoriteRepository mediaFavoriteRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    @Autowired
    public MediaFavoriteService(MediaFavoriteRepository mediaFavoriteRepository, MediaRepository mediaRepository, UserRepository userRepository) {
        this.mediaFavoriteRepository = mediaFavoriteRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public boolean toggleFavoriteMedia(UUID mediaId, UUID userId) {
        MediaFavoriteId favoriteId = new MediaFavoriteId(mediaId, userId);
        if (mediaFavoriteRepository.existsById(favoriteId)) {
            mediaFavoriteRepository.deleteById(favoriteId);
            return false;
        } else {
            Media media = mediaRepository.findById(mediaId)
                    .orElseThrow(() -> new RuntimeException("Media not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            MediaFavorite mediaFavorite = new MediaFavorite();
            mediaFavorite.setId(favoriteId);
            mediaFavorite.setMedia(media);
            mediaFavorite.setUser(user);
            mediaFavoriteRepository.save(mediaFavorite);
            return true;
        }
    }

    public long getFavoriteCount(UUID mediaId) {
        return mediaFavoriteRepository.countByMediaId(mediaId);
    }

    public List<Media> getFavoritesByUser(User user) {
        List<MediaFavorite> favorites = mediaFavoriteRepository.findAllByUser(user);
        return favorites.stream()
                .map(MediaFavorite::getMedia)
                .collect(Collectors.toList());
    }

    public void removeFavorite(User user, UUID mediaId) {
        Optional<MediaFavorite> favoriteOpt = mediaFavoriteRepository.findByUserAndMediaId(user, mediaId);
        favoriteOpt.ifPresent(mediaFavoriteRepository::delete);
    }
}