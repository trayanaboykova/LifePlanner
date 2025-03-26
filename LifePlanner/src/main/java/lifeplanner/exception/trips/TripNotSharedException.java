package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripNotSharedException extends RuntimeException {
    private final UUID tripId;
    public TripNotSharedException(UUID tripId) {
        super("Trip " + tripId + " is not currently shared");
        this.tripId = tripId;
    }
}