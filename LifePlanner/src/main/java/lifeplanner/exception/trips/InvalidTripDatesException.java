package lifeplanner.exception.trips;

public class InvalidTripDatesException extends RuntimeException {
    public InvalidTripDatesException(String message) {
        super(message);
    }
}