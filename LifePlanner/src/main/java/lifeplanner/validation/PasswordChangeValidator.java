package lifeplanner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.UserEditRequest;
import lifeplanner.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordChangeValidator implements ConstraintValidator<ValidPasswordChange, UserEditRequest> {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(UserEditRequest request, ConstraintValidatorContext context) {
        // If no new password is provided, nothing to validate.
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

        // Ensure current password is provided
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            context.buildConstraintViolationWithTemplate("Current password is required to change your password")
                    .addPropertyNode("currentPassword")
                    .addConstraintViolation();
            valid = false;
        } else {
            // Retrieve the currently authenticated user's details
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                // Get the user by username (make sure this method exists in your UserService)
                User user = userService.getByUsername(auth.getName());
                if (user != null && !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    context.buildConstraintViolationWithTemplate("Current password is incorrect.")
                            .addPropertyNode("currentPassword")
                            .addConstraintViolation();
                    valid = false;
                }
            } else {
                // If authentication info is not available, add a generic error.
                context.buildConstraintViolationWithTemplate("Unable to verify current password.")
                        .addPropertyNode("currentPassword")
                        .addConstraintViolation();
                valid = false;
            }
        }

        // Validate new password and confirm new password match
        if (request.getConfirmNewPassword() == null || !request.getNewPassword().equals(request.getConfirmNewPassword())) {
            context.buildConstraintViolationWithTemplate("New password and confirm new password do not match")
                    .addPropertyNode("confirmNewPassword")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}