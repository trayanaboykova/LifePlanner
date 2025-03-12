package lifeplanner.media.repository;

import lifeplanner.media.model.Media;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findAllByOwner(User user);

    List<Media> findAllByVisibleTrue();

    List<Media> findAllByApprovalStatus(ApprovalStatus pending);

}