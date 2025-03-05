package lifeplanner.goals.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String goalName;

    @Enumerated(EnumType.STRING)
    private GoalCategory category;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private GoalPriority priority;

    private Integer progress;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @Column(length = 2000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private boolean visible;

    @Transient
    private String progressBar;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoalLikes> likes;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoalFavorite> favorites;

}