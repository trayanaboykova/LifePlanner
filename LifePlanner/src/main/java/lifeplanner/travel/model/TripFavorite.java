package lifeplanner.travel.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TripFavorite {
    @EmbeddedId
    private TripFavoriteId id;

    @MapsId("tripId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Travel trip;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}