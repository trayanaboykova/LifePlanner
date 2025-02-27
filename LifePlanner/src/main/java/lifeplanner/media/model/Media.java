package lifeplanner.media.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMedia type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    @Column(nullable = false)
    private String title;

    private String genre;

    @Enumerated(EnumType.STRING)
    private MediaRating rating;

    private LocalDate dateRated;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private boolean visible;
}