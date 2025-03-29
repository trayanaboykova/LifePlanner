package lifeplanner.goals.repository;

import lifeplanner.goals.model.Goal;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findAllByOwner(User user);

    List<Goal> findAllByVisibleTrue();

    List<Goal> findAllByApprovalStatus(ApprovalStatus pending);

    List<Goal> findAllByVisibleTrueAndApprovalStatus(ApprovalStatus approvalStatus);

}