package lifeplanner.recipes.model;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RecipeLikesId {

    private UUID recipeId;

    private UUID userId;

    public RecipeLikesId(UUID recipeId, UUID userId) {
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public RecipeLikesId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeLikesId that = (RecipeLikesId) o;
        return Objects.equals(recipeId, that.recipeId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, userId);
    }
}
