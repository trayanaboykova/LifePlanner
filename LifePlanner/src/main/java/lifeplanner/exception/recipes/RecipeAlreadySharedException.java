package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipeAlreadySharedException extends RuntimeException {
    private final UUID recipeId;
    public RecipeAlreadySharedException(UUID recipeId) {
        super("Recipe " + recipeId + " is already shared");
        this.recipeId = recipeId;
    }
}