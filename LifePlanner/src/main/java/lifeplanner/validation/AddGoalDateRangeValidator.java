package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.AddGoalRequest;

public class AddGoalDateRangeValidator implements ConstraintValidator<ValidGoalDateRange, AddGoalRequest> {

    @Override
    public boolean isValid(AddGoalRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true; // or false, if you require both dates to be non-null
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