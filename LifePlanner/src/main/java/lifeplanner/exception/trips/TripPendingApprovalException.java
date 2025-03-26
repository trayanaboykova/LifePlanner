package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripPendingApprovalException extends RuntimeException {
    private final UUID tripId;
    public TripPendingApprovalException(UUID tripId) {
        super("Trip " + tripId + " is pending approval and cannot be shared.");
        this.tripId = tripId;
    }
}