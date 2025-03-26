package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripAlreadyApprovedException extends RuntimeException {
    private final UUID tripId;
    public TripAlreadyApprovedException(UUID tripId) {
        super("Trip " + tripId + " is already approved");
        this.tripId = tripId;
    }
}