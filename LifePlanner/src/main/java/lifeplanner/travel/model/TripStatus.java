package lifeplanner.travel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripStatus {
    UPCOMING("Upcoming"),
    FINISHED("Finished");

    private final String label;
}