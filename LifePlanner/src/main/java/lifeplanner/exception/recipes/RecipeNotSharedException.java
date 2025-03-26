package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipeNotSharedException extends RuntimeException {
    private final UUID recipeId;
    public RecipeNotSharedException(UUID recipeId) {
        super("Recipe " + recipeId + " is not currently shared");
        this.recipeId = recipeId;
    }
}