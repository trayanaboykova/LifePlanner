package lifeplanner.validation;

import jakarta.validation.ConstraintValidatorContext;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.UserEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordChangeValidatorUTest {

    @InjectMocks
    private PasswordChangeValidator validator;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    private void setupContextMocks() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
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
    void isValid_returnsFalse_whenCurrentPasswordIsNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword(null)
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
    void isValid_returnsFalse_whenNewPasswordTooShort() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("abc")  // too short (3 chars)
                .currentPassword("currentPass")
                .confirmNewPassword("abc")
                .build();

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password must be between 4 and 20 characters long");
        verify(builder).addPropertyNode("newPassword");

        // Update this to expect 2 calls since both validations fail
        verify(nodeBuilder, times(2)).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenNewPasswordTooLong() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("abcdefghijklmnopqrstuvwxyz")  // 26 characters (too long)
                .currentPassword("correctPass")
                .confirmNewPassword("abcdefghijklmnopqrstuvwxyz")
                .build();

        // Mock authentication to pass current password check
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        User mockUser = mock(User.class);
        when(userService.getByUsername("testUser")).thenReturn(mockUser);
        when(mockUser.getPassword()).thenReturn("encodedPass");
        when(passwordEncoder.matches("correctPass", "encodedPass")).thenReturn(true);

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password must be between 4 and 20 characters long");
        verify(builder).addPropertyNode("newPassword");
        verify(nodeBuilder).addConstraintViolation();  // Now only expecting one call
    }

    @Test
    void isValid_returnsFalse_whenPasswordsDontMatch() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("correctPass")  // Changed to match mock
                .confirmNewPassword("differentPass")
                .build();

        // Mock authentication to pass current password check
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        User mockUser = mock(User.class);
        when(userService.getByUsername("testUser")).thenReturn(mockUser);
        when(mockUser.getPassword()).thenReturn("encodedPass");
        when(passwordEncoder.matches("correctPass", "encodedPass")).thenReturn(true);

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password and confirm new password do not match");
        verify(builder).addPropertyNode("confirmNewPassword");
        verify(nodeBuilder).addConstraintViolation();  // Now only expecting one call
    }

    @Test
    void isValid_returnsFalse_whenConfirmPasswordIsNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("currentPass")
                .confirmNewPassword(null)
                .build();

        // Set up authentication so that current password check passes.
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        User mockUser = mock(User.class);
        when(userService.getByUsername("testUser")).thenReturn(mockUser);
        when(mockUser.getPassword()).thenReturn("encodedPass");
        when(passwordEncoder.matches("currentPass", "encodedPass")).thenReturn(true);

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("New password and confirm new password do not match");
        verify(builder).addPropertyNode("confirmNewPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenCurrentPasswordIncorrect() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("wrongPass")
                .confirmNewPassword("newPass")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        User mockUser = mock(User.class);
        when(userService.getByUsername("testUser")).thenReturn(mockUser);
        when(mockUser.getPassword()).thenReturn("encodedPass");
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Current password is incorrect.");
        verify(builder).addPropertyNode("currentPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenAuthenticationNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("currentPass")
                .confirmNewPassword("newPass")
                .build();

        when(securityContext.getAuthentication()).thenReturn(null);

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Unable to verify current password.");
        verify(builder).addPropertyNode("currentPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsFalse_whenAuthenticationNameNull() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("newPass")
                .currentPassword("currentPass")
                .confirmNewPassword("newPass")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);

        setupContextMocks();

        assertFalse(validator.isValid(userEditRequest, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Unable to verify current password.");
        verify(builder).addPropertyNode("currentPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_returnsTrue_whenAllConditionsMet() {
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .newPassword("validPass")
                .currentPassword("correctPass")
                .confirmNewPassword("validPass")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        User mockUser = mock(User.class);
        when(userService.getByUsername("testUser")).thenReturn(mockUser);
        when(mockUser.getPassword()).thenReturn("encodedPass");
        when(passwordEncoder.matches("correctPass", "encodedPass")).thenReturn(true);

        assertTrue(validator.isValid(userEditRequest, context));
    }
}