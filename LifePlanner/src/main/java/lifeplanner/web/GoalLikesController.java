package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import lifeplanner.goals.service.GoalLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalLikesController {

    private final GoalLikesService goalLikesService;

    @Autowired
    public GoalLikesController(GoalLikesService goalLikesService) {
        this.goalLikesService = goalLikesService;
    }

    @PostMapping("/{goalId}/like")
    public Map<String, Object> toggleGoalLike(@PathVariable UUID goalId,
                                              HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isLiked = goalLikesService.toggleLike(goalId, userId);
        long newCount = goalLikesService.getLikeCount(goalId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }


}
