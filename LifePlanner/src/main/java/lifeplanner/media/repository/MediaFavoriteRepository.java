package lifeplanner.media.repository;

import lifeplanner.media.model.MediaFavorite;
import lifeplanner.media.model.MediaFavoriteId;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaFavoriteRepository extends JpaRepository<MediaFavorite, MediaFavoriteId> {
    long countByMediaId(UUID mediaId);
    boolean existsById(MediaFavoriteId id);
    List<MediaFavorite> findAllByUser(User user);
}
