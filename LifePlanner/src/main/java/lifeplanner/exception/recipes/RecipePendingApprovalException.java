package lifeplanner.exception.recipes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RecipePendingApprovalException extends RuntimeException {
    private final UUID recipeId;
    public RecipePendingApprovalException(UUID recipeId) {
        super("Recipe " + recipeId + " is pending approval and cannot be shared.");
        this.recipeId = recipeId;
    }
}