package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalAlreadySharedException extends RuntimeException {
    private final UUID goalId;
    public GoalAlreadySharedException(UUID goalId) {
        super("Goal " + goalId + " is already shared");
        this.goalId = goalId;
    }
}