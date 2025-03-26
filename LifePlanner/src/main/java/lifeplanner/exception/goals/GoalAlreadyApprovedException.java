package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalAlreadyApprovedException extends RuntimeException {
    private final UUID goalId;
    public GoalAlreadyApprovedException(UUID goalId) {
        super("Goal " + goalId + " is already approved");
        this.goalId = goalId;
    }
}