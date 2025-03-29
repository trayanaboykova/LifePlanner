package lifeplanner.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordChangeValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidPasswordChange {
    String message() default "Invalid password change configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}