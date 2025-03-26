package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalNotFoundException extends RuntimeException {
    private final UUID goalId;
    public GoalNotFoundException(UUID goalId) {
        super("Goal not found with ID: " + goalId);
        this.goalId = goalId;
    }
}