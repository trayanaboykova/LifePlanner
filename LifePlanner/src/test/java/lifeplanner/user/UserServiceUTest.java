package lifeplanner.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.DomainException;
import lifeplanner.exception.user.AdminDeletionException;
import lifeplanner.exception.user.EmailAlreadyExistsException;
import lifeplanner.exception.user.PasswordChangeException;
import lifeplanner.exception.user.UsernameAlreadyExistsException;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.repository.UserRepository;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.RegisterRequest;
import lifeplanner.web.dto.UserEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setup() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encodedpassword")
                .email("test@example.com")
                .role(UserRole.USER)
                .isActive(true)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    // registerUser tests

    @Test
    void registerUserThrowsWhenUsernameExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setEmail("new@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.registerUser(registerRequest));
        assertTrue(ex.getMessage().contains("testuser"));
    }

    @Test
    void registerUserThrowsWhenEmailExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setEmail("test@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.registerUser(registerRequest));
        assertTrue(ex.getMessage().contains("test@example.com"));
    }

    @Test
    void registerUserThrowsWhenPasswordsDoNotMatch() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("different");
        registerRequest.setEmail("new@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        PasswordChangeException ex = assertThrows(PasswordChangeException.class,
                () -> userService.registerUser(registerRequest));
        assertTrue(ex.getMessage().contains("do not match"));
    }

    @Test
    void registerUserSucceedsWhenEmailIsNull() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("userNullEmail");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setEmail(null);  // Email is null
        registerRequest.setRole(UserRole.USER);

        when(userRepository.findByUsername("userNullEmail")).thenReturn(Optional.empty());

        userService.registerUser(registerRequest);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(UserRole.USER, saved.getRole());
    }

    @Test
    void registerUserSucceedsWhenEmailIsBlank() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("userBlankEmail");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setEmail("   ");
        registerRequest.setRole(UserRole.USER);

        when(userRepository.findByUsername("userBlankEmail")).thenReturn(Optional.empty());

        userService.registerUser(registerRequest);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("   ", saved.getEmail());
    }

    @Test
    void registerUserDefaultsRoleToUser() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("defaultRoleUser");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setEmail("default@role.com");
        registerRequest.setRole(null);

        when(userRepository.findByUsername("defaultRoleUser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("default@role.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.registerUser(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals(UserRole.USER, saved.getRole());
    }

    @Test
    void registerUserSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setEmail("new@example.com");
        registerRequest.setRole(UserRole.ADMIN);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword123");

        userService.registerUser(registerRequest);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedPassword123", savedUser.getPassword());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertTrue(savedUser.isActive());
        assertNotNull(savedUser.getRegistrationDate());
    }

    // getById tests

    @Test
    void getByIdReturnsUserWhenFound() {
        UUID id = sampleUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        User found = userService.getById(id);
        assertEquals(sampleUser, found);
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class,
                () -> userService.getById(id));
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    // editUserDetails tests

    @Test
    void editUserDetailsUpdatesProfileAndPassword() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedOldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setFirstName("John");
        userEditRequest.setLastName("Doe");
        userEditRequest.setEmail("john.doe@example.com");
        userEditRequest.setProfilePicture("newPic.jpg");
        userEditRequest.setRemoveProfilePic(false);
        userEditRequest.setCurrentPassword("oldPass");
        userEditRequest.setNewPassword("newPass");
        userEditRequest.setConfirmNewPassword("newPass");

        userService.editUserDetails(id, userEditRequest);

        assertEquals("John", sampleUser.getFirstName());
        assertEquals("Doe", sampleUser.getLastName());
        assertEquals("john.doe@example.com", sampleUser.getEmail());
        assertEquals("newPic.jpg", sampleUser.getProfilePicture());
        assertEquals("encodedNewPass", sampleUser.getPassword());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void editUserDetailsThrowsWhenCurrentPasswordMissing() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedOldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setNewPassword("newPass");
        userEditRequest.setConfirmNewPassword("newPass");
        // current password is missing

        PasswordChangeException ex = assertThrows(PasswordChangeException.class,
                () -> userService.editUserDetails(id, userEditRequest));
        assertTrue(ex.getMessage().contains("Current password is required"));
    }

    @Test
    void editUserDetailsThrowsWhenCurrentPasswordIncorrect() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedOldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPass", "encodedOldPassword")).thenReturn(false);

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setCurrentPassword("wrongPass");
        userEditRequest.setNewPassword("newPass");
        userEditRequest.setConfirmNewPassword("newPass");

        PasswordChangeException ex = assertThrows(PasswordChangeException.class,
                () -> userService.editUserDetails(id, userEditRequest));
        assertTrue(ex.getMessage().contains("incorrect"));
    }

    @Test
    void editUserDetailsThrowsWhenNewPasswordsMismatch() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedOldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setCurrentPassword("oldPass");
        userEditRequest.setNewPassword("newPass");
        userEditRequest.setConfirmNewPassword("different");

        PasswordChangeException passwordChangeException = assertThrows(PasswordChangeException.class,
                () -> userService.editUserDetails(id, userEditRequest));
        assertTrue(passwordChangeException.getMessage().contains("do not match"));
    }

    // Additional tests for editUserDetails: profile picture update and password change branches

    @Test
    void editUserDetailsRemovesProfilePic() {
        UUID id = sampleUser.getId();
        sampleUser.setProfilePicture("oldPic.jpg");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setRemoveProfilePic(true);
        userEditRequest.setProfilePicture("ignoredPic.jpg"); // should be ignored

        userService.editUserDetails(id, userEditRequest);

        assertNull(sampleUser.getProfilePicture());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void editUserDetailsUpdatesProfilePicWhenProvided() {
        UUID id = sampleUser.getId();
        sampleUser.setProfilePicture("oldPic.jpg");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setRemoveProfilePic(false);
        userEditRequest.setProfilePicture("newPic.jpg");

        userService.editUserDetails(id, userEditRequest);

        assertEquals("newPic.jpg", sampleUser.getProfilePicture());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void editUserDetailsDoesNotUpdateProfilePicWhenNotProvided() {
        UUID id = sampleUser.getId();
        sampleUser.setProfilePicture("oldPic.jpg");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setRemoveProfilePic(false);
        userEditRequest.setProfilePicture("   ");

        userService.editUserDetails(id, userEditRequest);

        assertEquals("oldPic.jpg", sampleUser.getProfilePicture());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void editUserDetailsSkipsPasswordChangeWhenNewPasswordBlank() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setFirstName("Jane");
        userEditRequest.setCurrentPassword("any");
        userEditRequest.setNewPassword("   ");
        userEditRequest.setConfirmNewPassword("   ");

        userService.editUserDetails(id, userEditRequest);

        assertEquals("encodedPassword", sampleUser.getPassword());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void editUserDetailsThrowsWhenCurrentPasswordIsBlank() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedOldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        UserEditRequest userEditRequest = UserEditRequest.builder().build();
        userEditRequest.setCurrentPassword("   ");
        userEditRequest.setNewPassword("newPass");
        userEditRequest.setConfirmNewPassword("newPass");

        PasswordChangeException ex = assertThrows(PasswordChangeException.class,
                () -> userService.editUserDetails(id, userEditRequest));
        assertTrue(ex.getMessage().contains("Current password is required"));
    }

    @Test
    void editUserDetailsThrowsWhenConfirmNewPasswordIsNull() {
        UUID id = sampleUser.getId();
        sampleUser.setPassword("encodedOldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);

        UserEditRequest req = UserEditRequest.builder().build();
        req.setCurrentPassword("oldPass");
        req.setNewPassword("newPass");
        req.setConfirmNewPassword(null);

        PasswordChangeException ex = assertThrows(PasswordChangeException.class,
                () -> userService.editUserDetails(id, req));
        assertTrue(ex.getMessage().contains("New password and confirm new password do not match"));
    }

    // getAllUsers test

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(sampleUser);
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals(sampleUser, result.get(0));
    }

    // switchRole tests

    @Test
    void testSwitchRoleTogglesFromUserToAdmin() {
        UUID id = sampleUser.getId();
        sampleUser.setRole(UserRole.USER);
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        userService.switchRole(id);
        assertEquals(UserRole.ADMIN, sampleUser.getRole());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testSwitchRoleTogglesFromAdminToUser() {
        UUID id = sampleUser.getId();
        sampleUser.setRole(UserRole.ADMIN);
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        userService.switchRole(id);
        assertEquals(UserRole.USER, sampleUser.getRole());
        verify(userRepository).save(sampleUser);
    }

    // switchStatus tests

    @Test
    void testSwitchStatusTogglesActiveFlag() {
        UUID id = sampleUser.getId();
        sampleUser.setActive(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));

        userService.switchStatus(id);
        assertFalse(sampleUser.isActive());
        verify(userRepository).save(sampleUser);
    }

    // loadUserByUsername tests

    @Test
    void testLoadUserByUsernameReturnsUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        UserDetails details = userService.loadUserByUsername("testuser");
        assertTrue(details instanceof AuthenticationMetadata);
        AuthenticationMetadata auth = (AuthenticationMetadata) details;
        assertEquals(sampleUser.getId(), auth.getUserId());
        assertEquals("testuser", auth.getUsername());
        assertEquals(sampleUser.getPassword(), auth.getPassword());
        assertEquals(sampleUser.getRole(), auth.getRole());
        assertEquals(sampleUser.isActive(), auth.isActive());
    }

    @Test
    void testLoadUserByUsernameThrowsWhenNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class,
                () -> userService.loadUserByUsername("nonexistent"));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    // updateUser test

    @Test
    void testUpdateUserCallsRepositorySave() {
        userService.updateUser(sampleUser);
        verify(userRepository).save(sampleUser);
    }

    // deactivateUserProfile test

    @Test
    void testDeactivateUserProfileSetsActiveFalse() {
        UUID id = sampleUser.getId();
        sampleUser.setActive(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        userService.deactivateUserProfile(id);
        assertFalse(sampleUser.isActive());
        verify(userRepository).save(sampleUser);
    }

    // deleteUserById tests

    @Test
    void testDeleteUserByIdThrowsWhenUserIsAdmin() {
        UUID id = sampleUser.getId();
        sampleUser.setRole(UserRole.ADMIN);
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        AdminDeletionException ex = assertThrows(AdminDeletionException.class,
                () -> userService.deleteUserById(id));
        assertTrue(ex.getMessage().contains("Cannot delete admin user"));
    }

    @Test
    void testDeleteUserByIdDeletesUserWhenNotAdmin() {
        UUID id = sampleUser.getId();
        sampleUser.setRole(UserRole.USER);
        when(userRepository.findById(id)).thenReturn(Optional.of(sampleUser));
        userService.deleteUserById(id);
        verify(userRepository).deleteById(id);
    }
}