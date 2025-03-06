package lifeplanner.travel.repository;

import lifeplanner.travel.model.Travel;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface TravelRepository extends JpaRepository<Travel, UUID> {

    List<Travel> findAllByOwner(User user);

    List<Travel> findAllByVisibleTrue();

    List<Travel> findAllByApprovalStatus(ApprovalStatus pending);
}
