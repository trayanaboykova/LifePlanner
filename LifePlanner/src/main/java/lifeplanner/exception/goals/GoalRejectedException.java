package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalRejectedException extends RuntimeException {
    private final UUID goalId;
    public GoalRejectedException(UUID goalId) {
        super("Goal " + goalId + " was rejected and cannot be shared. Please edit and resubmit.");
        this.goalId = goalId;
    }
}