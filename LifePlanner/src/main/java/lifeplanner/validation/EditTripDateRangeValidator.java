package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.EditTripRequest;

public class EditTripDateRangeValidator implements ConstraintValidator<ValidDateRange, EditTripRequest> {
    @Override
    public boolean isValid(EditTripRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true; // Or false, based on your requirements
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}