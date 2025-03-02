package lifeplanner.goals.repository;

import lifeplanner.goals.model.GoalLikes;
import lifeplanner.goals.model.GoalLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GoalLikesRepository extends JpaRepository<GoalLikes, GoalLikesId> {

    // Count how many likes a goal has
    long countByGoalId(UUID bookId);

    // Check if a user has already liked a goal
    boolean existsById(GoalLikesId id);
}
