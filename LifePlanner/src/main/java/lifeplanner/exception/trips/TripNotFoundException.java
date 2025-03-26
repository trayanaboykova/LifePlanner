package lifeplanner.exception.trips;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TripNotFoundException extends RuntimeException {
    private final UUID tripId;
    public TripNotFoundException(UUID tripId) {
        super("Recipe not found with ID: " + tripId);
        this.tripId = tripId;
    }
}