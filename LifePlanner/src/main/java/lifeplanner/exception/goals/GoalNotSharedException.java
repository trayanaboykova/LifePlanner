package lifeplanner.exception.goals;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GoalNotSharedException extends RuntimeException {
  private final UUID goalId;
    public GoalNotSharedException(UUID goalId) {
      super("Goal " + goalId + " is not currently shared");
        this.goalId = goalId;
    }
}