package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripAlreadySharedException extends RuntimeException {
    private final UUID tripId;
    public TripAlreadySharedException(UUID tripId) {
        super("Trip " + tripId + " is already shared");
        this.tripId = tripId;
    }
}