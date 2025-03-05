package lifeplanner.web.goals;

import jakarta.servlet.http.HttpSession;
import lifeplanner.goals.service.GoalFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalFavoriteController {

    private final GoalFavoriteService goalFavoriteService;

    @Autowired
    public GoalFavoriteController(GoalFavoriteService goalFavoriteService) {
        this.goalFavoriteService = goalFavoriteService;
    }

    @PostMapping("/{goalId}/favorite")
    public Map<String, Object> toggleGoalFavorite(@PathVariable UUID goalId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isFavorited = goalFavoriteService.toggleFavorite(goalId, userId);
        long newCount = goalFavoriteService.getFavoriteCount(goalId);

        return Map.of(
                "favorited", isFavorited,
                "favoriteCount", newCount
        );
    }
}
