package lifeplanner.web.recipes;

import jakarta.servlet.http.HttpSession;
import lifeplanner.recipes.service.RecipeFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeFavoriteController {
    private final RecipeFavoriteService recipeFavoriteService;

    @Autowired
    public RecipeFavoriteController(RecipeFavoriteService bookFavoriteService) {
        this.recipeFavoriteService = bookFavoriteService;
    }

    @PostMapping("/{recipeId}/favorite")
    public Map<String, Object> toggleRecipeFavorite(@PathVariable UUID recipeId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isFavorited = recipeFavoriteService.toggleFavorite(recipeId, userId);
        long newCount = recipeFavoriteService.getFavoriteCount(recipeId);

        return Map.of(
                "favorited", isFavorited,
                "favoriteCount", newCount
        );
    }
}