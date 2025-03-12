package lifeplanner.web.goals;

import lifeplanner.goals.service.GoalLikesService;
import lifeplanner.security.AuthenticationMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                                              @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        boolean isLiked = goalLikesService.toggleLike(goalId, userId);
        long newCount = goalLikesService.getLikeCount(goalId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }
}