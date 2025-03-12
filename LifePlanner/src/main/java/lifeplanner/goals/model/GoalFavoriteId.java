package lifeplanner.goals.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class GoalFavoriteId implements Serializable {

    private UUID goalId;
    private UUID userId;

    public GoalFavoriteId() {
    }

    public GoalFavoriteId(UUID goalId, UUID userId) {
        this.goalId = goalId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GoalFavoriteId that = (GoalFavoriteId) o;
        return Objects.equals(goalId, that.goalId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalId, userId);
    }
}