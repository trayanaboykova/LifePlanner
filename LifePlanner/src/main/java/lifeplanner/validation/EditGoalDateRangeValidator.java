package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.EditGoalRequest;

public class EditGoalDateRangeValidator implements ConstraintValidator<ValidGoalDateRange, EditGoalRequest> {

    @Override
    public boolean isValid(EditGoalRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true; // or false, based on your requirements
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