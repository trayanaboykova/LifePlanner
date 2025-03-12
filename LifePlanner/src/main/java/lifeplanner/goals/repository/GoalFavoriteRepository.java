package lifeplanner.goals.repository;

import lifeplanner.goals.model.GoalFavorite;
import lifeplanner.goals.model.GoalFavoriteId;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalFavoriteRepository extends JpaRepository<GoalFavorite, GoalFavoriteId> {

    long countByGoalId(UUID goalId);

    boolean existsById(GoalFavoriteId id);

    List<GoalFavorite> findAllByUser(User user);

    Optional<GoalFavorite> findByUserAndGoalId(User user, UUID goalId);

}