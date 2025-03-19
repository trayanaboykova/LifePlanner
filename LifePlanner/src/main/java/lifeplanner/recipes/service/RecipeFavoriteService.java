package lifeplanner.recipes.service;

import lifeplanner.exception.DomainException;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeFavorite;
import lifeplanner.recipes.model.RecipeFavoriteId;
import lifeplanner.recipes.repository.RecipeFavoriteRepository;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeFavoriteService {
    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeFavoriteService(RecipeFavoriteRepository recipeFavoriteRepository, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeFavoriteRepository = recipeFavoriteRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public long getFavoriteCount(UUID recipeId) {
        return recipeFavoriteRepository.countByRecipeId(recipeId);
    }

    public boolean toggleFavorite(UUID recipeId, UUID userId) {
        RecipeFavoriteId favoriteId = new RecipeFavoriteId(recipeId, userId);
        if (recipeFavoriteRepository.existsById(favoriteId)) {
            recipeFavoriteRepository.deleteById(favoriteId);
            return false; // now unfavorited
        } else {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new DomainException("Recipe not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DomainException("User not found"));

            RecipeFavorite favorite = new RecipeFavorite();
            favorite.setId(favoriteId);
            favorite.setRecipe(recipe);
            favorite.setUser(user);
            recipeFavoriteRepository.save(favorite);
            return true; // now favorited
        }
    }

    public List<Recipe> getFavoritesByUser(User user) {
        List<RecipeFavorite> favorites = recipeFavoriteRepository.findAllByUser(user);
        return favorites.stream()
                .map(RecipeFavorite::getRecipe)
                .collect(Collectors.toList());
    }

    public void removeFavorite(User user, UUID recipeId) {
        Optional<RecipeFavorite> favoriteOpt = recipeFavoriteRepository.findByUserAndRecipeId(user, recipeId);
        favoriteOpt.ifPresent(recipeFavoriteRepository::delete);
    }
}