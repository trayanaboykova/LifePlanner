package lifeplanner.media.repository;

import lifeplanner.books.model.BookLikes;
import lifeplanner.books.model.BookLikesId;
import lifeplanner.media.model.MediaLikes;
import lifeplanner.media.model.MediaLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaLikesRepository extends JpaRepository<MediaLikes, MediaLikesId> {
    // Count how many likes a media type has
    long countByMediaId(UUID bookId);

    // Check if a user has already liked a type of media
    boolean existsById(MediaLikesId id);

}
