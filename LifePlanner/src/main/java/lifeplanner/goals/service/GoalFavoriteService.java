package lifeplanner.goals.service;

import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalFavorite;
import lifeplanner.goals.model.GoalFavoriteId;
import lifeplanner.goals.repository.GoalFavoriteRepository;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoalFavoriteService {
    private final GoalFavoriteRepository goalFavoriteRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Autowired
    public GoalFavoriteService(GoalFavoriteRepository goalFavoriteRepository, GoalRepository goalRepository, UserRepository userRepository) {
        this.goalFavoriteRepository = goalFavoriteRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public long getFavoriteCount(UUID goalId) {
        return goalFavoriteRepository.countByGoalId(goalId);
    }

    public boolean toggleFavorite(UUID goalId, UUID userId) {
        GoalFavoriteId favoriteId = new GoalFavoriteId(goalId, userId);
        if (goalFavoriteRepository.existsById(favoriteId)) {
            goalFavoriteRepository.deleteById(favoriteId);
            return false; // now unfavorited
        } else {
            Goal goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            GoalFavorite favorite = new GoalFavorite();
            favorite.setId(favoriteId);
            favorite.setGoal(goal);
            favorite.setUser(user);
            goalFavoriteRepository.save(favorite);
            return true; // now favorited
        }
    }

    public List<Goal> getFavoritesByUser(User user) {
        List<GoalFavorite> favorites = goalFavoriteRepository.findAllByUser(user);
        return favorites.stream()
                .map(GoalFavorite::getGoal)
                .collect(Collectors.toList());
    }

    public void removeFavorite(User user, UUID goalId) {
        Optional<GoalFavorite> favoriteOpt = goalFavoriteRepository.findByUserAndGoalId(user, goalId);
        favoriteOpt.ifPresent(goalFavoriteRepository::delete);
    }
}