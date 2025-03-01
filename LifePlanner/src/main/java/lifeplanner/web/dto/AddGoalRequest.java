package lifeplanner.web.dto;


import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.goals.model.GoalCategory;
import lifeplanner.goals.model.GoalPriority;
import lifeplanner.goals.model.GoalStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddGoalRequest {

    @NotNull
    @Size(min = 2, max = 50, message = "Goal name length must be between 2 and 50 characters!")
    private String goalName;

    private GoalCategory category;

    private LocalDate startDate;

    private LocalDate endDate;

    private GoalPriority priority;

    private Integer progress;

    @NotNull(message = "You must select status!")
    private GoalStatus status;

    @Lob
    private String notes;
}