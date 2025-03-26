package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalPendingApprovalException extends RuntimeException {
    private final UUID goalId;
    public GoalPendingApprovalException(UUID goalId) {
        super("Goal " + goalId + " is pending approval and cannot be shared.");
        this.goalId = goalId;
    }
}