package lifeplanner.web.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.travel.model.TransportationType;
import lifeplanner.travel.model.TripStatus;
import lifeplanner.travel.model.TripType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddTripRequest {

    @NotNull(message = "You must select status!")
    private TripStatus tripStatus;

    @NotNull
    @Size(min = 2, max = 50, message = "Name length must be between 2 and 50 characters!")
    private String tripName;

    @NotNull
    @Size(min = 2, max = 50, message = "Destination input length must be between 2 and 100 characters!")
    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    private TripType tripType;

    @Size(max = 50, message = "Accommodation input length must be between 2 and 100 characters!")
    private String accommodation;

    private TransportationType transportationType;

    @Lob
    private String notes;

    @AssertTrue(message = "Start date must be before or equal to end date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true; // or false if both dates are required
        }
        return !startDate.isAfter(endDate);
    }
}