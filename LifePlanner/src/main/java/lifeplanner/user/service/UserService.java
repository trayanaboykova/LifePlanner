package lifeplanner.user.service;

import jakarta.validation.Valid;
import lifeplanner.exception.*;
import lifeplanner.exception.user.AdminDeletionException;
import lifeplanner.exception.user.EmailAlreadyExistsException;
import lifeplanner.exception.user.PasswordChangeException;
import lifeplanner.exception.user.UsernameAlreadyExistsException;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.repository.UserRepository;
import lifeplanner.web.dto.RegisterRequest;
import lifeplanner.web.dto.UserEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistsException("Username [%s] already exists.".formatted(registerRequest.getUsername()));
        }

        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isBlank() &&
                userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email [%s] is already in use.".formatted(registerRequest.getEmail()));
        }

        // Validate password and confirmPassword
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new PasswordChangeException("Password and confirm password do not match.");
        }

        // Use the role from the RegisterRequest, default to USER if not provided
        UserRole role = registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.USER;

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(role)
                .isActive(true)
                .registrationDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DomainException("User with id [" + userId + "] does not exist."));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void editUserDetails(UUID id, @Valid UserEditRequest userEditRequest) {
        User user = getById(id);

        // Update basic profile details
        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());

        // Handle profile picture update
        if (userEditRequest.isRemoveProfilePic()) {
            user.setProfilePicture(null); // Remove profile picture if checkbox is checked
        } else if (userEditRequest.getProfilePicture() != null && !userEditRequest.getProfilePicture().isBlank()) {
            user.setProfilePicture(userEditRequest.getProfilePicture()); // Update profile picture
        }

        // Handle password change only if newPassword is provided
        if (userEditRequest.getNewPassword() != null && !userEditRequest.getNewPassword().isBlank()) {
            // Ensure that the user has entered the current password
            if (userEditRequest.getCurrentPassword() == null || userEditRequest.getCurrentPassword().isBlank()) {
                throw new PasswordChangeException("Current password is required to change your password.");
            }
            // Validate current password
            if (!passwordEncoder.matches(userEditRequest.getCurrentPassword(), user.getPassword())) {
                throw new PasswordChangeException("Current password is incorrect.");
            }
            // Validate that the new password and confirmation match
            if (userEditRequest.getConfirmNewPassword() == null ||
                    !userEditRequest.getNewPassword().equals(userEditRequest.getConfirmNewPassword())) {
                throw new PasswordChangeException("New password and confirm new password do not match.");
            }
            // Update the password
            user.setPassword(passwordEncoder.encode(userEditRequest.getNewPassword()));
        }

        userRepository.save(user);
    }

    @Cacheable("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("User with username [" + username + "] does not exist."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);
        return new AuthenticationMetadata(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {
        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {
        User user = getById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deactivateUserProfile(UUID id) {
        User user = getById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deleteUserById(UUID id) {
        User user = getById(id);
        if(user.getRole() == UserRole.ADMIN) {
            throw new AdminDeletionException("Cannot delete admin user.");
        }
        userRepository.deleteById(id);
    }
}