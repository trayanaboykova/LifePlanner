package lifeplanner.recipes.service;

import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeLikes;
import lifeplanner.recipes.model.RecipeLikesId;
import lifeplanner.recipes.repository.RecipeLikesRepository;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RecipeLikesService {

    private final RecipeLikesRepository recipeLikesRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeLikesService(RecipeLikesRepository recipeLikesRepository, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeLikesRepository = recipeLikesRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public long getLikeCount(UUID recipeId) {
        return recipeLikesRepository.countByRecipeId(recipeId);
    }

    public boolean toggleLike(UUID recipeId, UUID userId) {
        RecipeLikesId likeId = new RecipeLikesId(recipeId, userId);

        if (recipeLikesRepository.existsById(likeId)) {
            recipeLikesRepository.deleteById(likeId);
            return false;
        } else {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            RecipeLikes newlike = new RecipeLikes();
            newlike.setId(likeId);
            newlike.setRecipe(recipe);
            newlike.setUser(user);
            recipeLikesRepository.save(newlike);
            return true;
        }
    }
}