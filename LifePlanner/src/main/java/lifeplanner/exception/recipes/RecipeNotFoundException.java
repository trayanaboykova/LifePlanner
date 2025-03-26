package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipeNotFoundException extends RuntimeException {
    private final UUID recipeId;
    public RecipeNotFoundException(UUID recipeId) {
        super("Recipe not found with ID: " + recipeId);
        this.recipeId = recipeId;
    }
}