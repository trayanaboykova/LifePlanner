package lifeplanner.web.recipes;

import jakarta.servlet.http.HttpSession;
import lifeplanner.recipes.service.RecipeLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/recipes")
public class RecipeLikesController {

    private final RecipeLikesService recipeLikesService;

    @Autowired
    public RecipeLikesController(RecipeLikesService recipeLikesService) {
        this.recipeLikesService = recipeLikesService;
    }

    @PostMapping("/{recipeId}/like")
    public Map<String, Object> toggleRecipeLike(@PathVariable UUID recipeId,
                                              HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        boolean isLiked = recipeLikesService.toggleLike(recipeId, userId);
        long newCount = recipeLikesService.getLikeCount(recipeId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }
}
