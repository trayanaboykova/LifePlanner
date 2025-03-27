package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.UserEditRequest;

public class PasswordChangeValidator implements ConstraintValidator<ValidPasswordChange, UserEditRequest> {

    @Override
    public boolean isValid(UserEditRequest request, ConstraintValidatorContext context) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (request.getNewPassword().length() < 4 || request.getNewPassword().length() > 20) {
            context.buildConstraintViolationWithTemplate("New password must be between 4 and 20 characters long")
                    .addPropertyNode("newPassword")
                    .addConstraintViolation();
            valid = false;
        }

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            context.buildConstraintViolationWithTemplate("Current password is required to change your password")
                    .addPropertyNode("currentPassword")
                    .addConstraintViolation();
            valid = false;
        }

        if (request.getConfirmNewPassword() == null || !request.getNewPassword().equals(request.getConfirmNewPassword())) {
            context.buildConstraintViolationWithTemplate("New password and confirm new password do not match")
                    .addPropertyNode("confirmNewPassword")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}