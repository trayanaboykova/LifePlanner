package lifeplanner.travel.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TripFavoriteId implements Serializable {

    private UUID tripId;
    private UUID userId;

    public TripFavoriteId(UUID tripId, UUID userId) {
        this.tripId = tripId;
        this.userId = userId;
    }

    public TripFavoriteId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TripFavoriteId that = (TripFavoriteId) o;
        return Objects.equals(tripId, that.tripId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, userId);
    }
}