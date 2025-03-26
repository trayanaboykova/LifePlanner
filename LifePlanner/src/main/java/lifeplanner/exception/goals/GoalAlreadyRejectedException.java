package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalAlreadyRejectedException extends RuntimeException {
    private final UUID goalId;
    public GoalAlreadyRejectedException(UUID goalId) {
        super("Goal " + goalId + " is already rejected");
        this.goalId = goalId;
    }
}