package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipeAlreadyApprovedException extends RuntimeException {
    private final UUID recipeId;
    public RecipeAlreadyApprovedException(UUID recipeId) {
        super("Recipe " + recipeId + " is already approved");
        this.recipeId = recipeId;
    }
}