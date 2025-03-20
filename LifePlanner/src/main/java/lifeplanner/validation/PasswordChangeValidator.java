package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.UserEditRequest;

public class PasswordChangeValidator implements ConstraintValidator<ValidPasswordChange, UserEditRequest> {

    @Override
    public boolean isValid(UserEditRequest request, ConstraintValidatorContext context) {
        // If no new password is provided, skip password-related validation.
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // Validate new password length
        if (request.getNewPassword().length() < 4 || request.getNewPassword().length() > 20) {
            context.buildConstraintViolationWithTemplate("New password must be between 4 and 20 characters long")
                    .addPropertyNode("newPassword")
                    .addConstraintViolation();
            valid = false;
        }
        // Validate that current password is provided
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            context.buildConstraintViolationWithTemplate("Current password is required to change your password")
                    .addPropertyNode("currentPassword")
                    .addConstraintViolation();
            valid = false;
        }
        // Validate that confirm new password is provided and matches new password
        if (request.getConfirmNewPassword() == null || !request.getNewPassword().equals(request.getConfirmNewPassword())) {
            context.buildConstraintViolationWithTemplate("New password and confirm new password do not match")
                    .addPropertyNode("confirmNewPassword")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
