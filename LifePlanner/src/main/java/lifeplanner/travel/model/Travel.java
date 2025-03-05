package lifeplanner.travel.model;

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
public class Travel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus;

    @Column(nullable = false)
    private String tripName;

    @Column(nullable = false)
    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    private String accommodation;

    @Enumerated(EnumType.STRING)
    private TransportationType transportation;

    @Column(length = 2000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private boolean visible;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripLikes> likes;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripFavorite> favorites;

}