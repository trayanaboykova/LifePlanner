package lifeplanner.goals.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalCategory {
    HEALTH_AND_FITNESS("Health & Fitness"),
    CAREER_AND_EDUCATION("Career & Education"),
    FINANCE("Finance"),
    PERSONAL_GROWTH("Personal Growth"),
    TRAVEL("Travel"),
    RELATIONSHIPS("Relationships");

    private final String label;
}
