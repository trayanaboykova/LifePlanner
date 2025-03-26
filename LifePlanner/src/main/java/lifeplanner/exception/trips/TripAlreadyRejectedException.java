package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripAlreadyRejectedException extends RuntimeException {
    private final UUID tripId;
    public TripAlreadyRejectedException(UUID tripId) {
        super("Trip " + tripId + " is already rejected");
        this.tripId = tripId;
    }
}