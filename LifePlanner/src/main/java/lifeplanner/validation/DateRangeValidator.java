package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.AddTripRequest;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, AddTripRequest> {
    @Override
    public boolean isValid(AddTripRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true; // or false, if dates are required
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            // Disable the default global violation
            context.disableDefaultConstraintViolation();
            // Attach error to the endDate field
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}