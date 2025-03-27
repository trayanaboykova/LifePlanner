package lifeplanner.validation;

import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.web.dto.UserEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordChangeValidatorUTest {

    private PasswordChangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setup() {
        validator = new PasswordChangeValidator();
    }

    @Test
    void isValid_returnsTrue_whenNewPasswordIsNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword(null)
                .build();

        assertTrue(validator.isValid(userEditRequest, context));
        verifyNoInteractions(context);
    }

    @Test
    void isValid_returnsTrue_whenNewPasswordIsBlank() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("   ")
                .build();

        assertTrue(validator.isValid(userEditRequest, context));
        verifyNoInteractions(context);
    }

    @Test
    void isValid_returnsFalse_whenNewPasswordTooShort() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("abc")
                .currentPassword("current")
                .confirmNewPassword("abc")
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password must be between 4 and 20 characters long");
        verify(builder).addPropertyNode("newPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenNewPasswordTooLong() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("a".repeat(21))
                .currentPassword("current")
                .confirmNewPassword("a".repeat(21))
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password must be between 4 and 20 characters long");
        verify(builder).addPropertyNode("newPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenCurrentPasswordIsBlank() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("   ")
                .confirmNewPassword("newPass")
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Current password is required to change your password");
        verify(builder).addPropertyNode("currentPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenConfirmNewPasswordIsNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("currentPass")
                .confirmNewPassword(null)
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password and confirm new password do not match");
        verify(builder).addPropertyNode("confirmNewPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenConfirmNewPasswordDoesNotMatch() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("currentPass")
                .confirmNewPassword("different")
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password and confirm new password do not match");
        verify(builder).addPropertyNode("confirmNewPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsTrue_whenAllPasswordFieldsValid() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("validPass")
                .currentPassword("currentPass")
                .confirmNewPassword("validPass")
                .build();

        assertTrue(validator.isValid(userEditRequest, context));
        verify(context).disableDefaultConstraintViolation();
        verifyNoMoreInteractions(context);
    }

    private void setupContextMocks() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValid_returnsFalse_whenCurrentPasswordIsNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword(null)  // explicitly setting to null
                .confirmNewPassword("newPass")
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Current password is required to change your password");
        verify(builder).addPropertyNode("currentPassword");
        verify(nodeBuilder).addConstraintViolation();
    }
}