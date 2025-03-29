package lifeplanner.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = { AddGoalDateRangeValidator.class, EditGoalDateRangeValidator.class })
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGoalDateRange {
    String message() default "Start date must be before or equal to end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}