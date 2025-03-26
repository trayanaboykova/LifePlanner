package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripRejectedException extends RuntimeException {
    private final UUID tripId;
    public TripRejectedException(UUID tripId) {
        super("Trip " + tripId + " was rejected and cannot be shared. Please edit and resubmit.");
        this.tripId = tripId;
    }
}