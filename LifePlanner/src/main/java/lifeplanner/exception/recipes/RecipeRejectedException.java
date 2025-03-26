package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipeRejectedException extends RuntimeException {
    private final UUID recipeId;
    public RecipeRejectedException(UUID recipeId) {
        super("Recipe " + recipeId + " was rejected and cannot be shared. Please edit and resubmit.");
        this.recipeId = recipeId;
    }
}