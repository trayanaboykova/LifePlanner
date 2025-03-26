package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipeAlreadyRejectedException extends RuntimeException {
    private final UUID recipeId;
    public RecipeAlreadyRejectedException(UUID recipeId) {
        super("Recipe " + recipeId + " is already rejected");
        this.recipeId = recipeId;
    }
}