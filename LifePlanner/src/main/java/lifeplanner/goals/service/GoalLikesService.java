package lifeplanner.goals.service;

import lifeplanner.exception.DomainException;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalLikes;
import lifeplanner.goals.model.GoalLikesId;
import lifeplanner.goals.repository.GoalLikesRepository;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GoalLikesService {

    private final GoalLikesRepository goalLikesRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Autowired
    public GoalLikesService(GoalLikesRepository goalLikesRepository, GoalRepository goalRepository, UserRepository userRepository) {
        this.goalLikesRepository = goalLikesRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public long getLikeCount(UUID goalId) {
        return goalLikesRepository.countByGoalId(goalId);
    }

    public boolean toggleLike(UUID goalId, UUID userId) {
        GoalLikesId likeId = new GoalLikesId(goalId, userId);

        // If already liked, remove the like
        if (goalLikesRepository.existsById(likeId)) {
            goalLikesRepository.deleteById(likeId);
            return false; // false means "now unliked"
        } else {
            // Otherwise, create a new like
            Goal goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new DomainException("Goal not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DomainException("User not found"));

            GoalLikes newLike = new GoalLikes();
            newLike.setId(likeId);
            newLike.setGoal(goal);
            newLike.setUser(user);
            goalLikesRepository.save(newLike);
            return true; // true means "now liked"
        }
    }
}