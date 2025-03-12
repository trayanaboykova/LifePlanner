package lifeplanner.goals.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String label;
}