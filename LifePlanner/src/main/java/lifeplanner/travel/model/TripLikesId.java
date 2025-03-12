package lifeplanner.travel.model;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TripLikesId {

    private UUID tripId;
    private UUID userId;

    public TripLikesId(UUID tripId, UUID userId) {
        this.tripId = tripId;
        this.userId = userId;
    }

    public TripLikesId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TripLikesId that = (TripLikesId) o;
        return Objects.equals(tripId, that.tripId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, userId);
    }
}