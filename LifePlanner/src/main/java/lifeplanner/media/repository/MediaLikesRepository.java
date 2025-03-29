package lifeplanner.media.repository;

import lifeplanner.media.model.MediaLikes;
import lifeplanner.media.model.MediaLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaLikesRepository extends JpaRepository<MediaLikes, MediaLikesId> {

    long countByMediaId(UUID bookId);

    boolean existsById(MediaLikesId id);

}