package lifeplanner.web.recipes;

import lifeplanner.recipes.service.RecipeLikesService;
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
@RequestMapping("api/recipes")
public class RecipeLikesController {

    private final RecipeLikesService recipeLikesService;

    @Autowired
    public RecipeLikesController(RecipeLikesService recipeLikesService) {
        this.recipeLikesService = recipeLikesService;
    }

    @PostMapping("/{recipeId}/like")
    public Map<String, Object> toggleRecipeLike(@PathVariable UUID recipeId,
                                                @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        boolean isLiked = recipeLikesService.toggleLike(recipeId, userId);
        long newCount = recipeLikesService.getLikeCount(recipeId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }
}