package lifeplanner.goals.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoalFavorite {
    @EmbeddedId
    private GoalFavoriteId id;

    @MapsId("goalId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
