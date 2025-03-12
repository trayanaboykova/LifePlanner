package lifeplanner.travel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransportationType {
    AIRPLANE("Airplane"),
    CAR("Car"),
    CAR_RENTAL("Car Rental"),
    TRAIN("Train"),
    BUS("Bus"),
    SHIP("Ship");

    private final String label;
}