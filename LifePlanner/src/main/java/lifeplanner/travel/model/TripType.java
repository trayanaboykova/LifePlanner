package lifeplanner.travel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripType {
    VACATION("Vacation"),
    BUSINESS("Business"),
    FAMILY_TRIP("Family Trip"),
    ADVENTURE("Adventure"),
    ROAD_TRIP("Road Trip"),
    SOLO_TRAVEL("Solo Travel"),;

    private final String label;
}
