package lifeplanner.recipes.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RecipeFavoriteId implements Serializable {
    private UUID recipeId;
    private UUID userId;

    public RecipeFavoriteId(UUID recipeId, UUID userId) {
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public RecipeFavoriteId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeFavoriteId that = (RecipeFavoriteId) o;
        return Objects.equals(recipeId, that.recipeId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, userId);
    }
}