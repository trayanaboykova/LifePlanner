package lifeplanner.user.service;

import jakarta.validation.Valid;
import lifeplanner.exception.DomainException;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.repository.UserRepository;
import lifeplanner.web.dto.LoginRequest;
import lifeplanner.web.dto.RegisterRequest;
import lifeplanner.web.dto.UserEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername((registerRequest.getUsername()));

        if (optionalUser.isPresent()) {
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(UserRole.USER)
                .isActive(true)
                .registrationDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public User loginUser(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Incorrect username or password.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect username or password.");
        }

        return user;
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with id [" + userId + "] does not exist."));
    }

    public void editUserDetails(UUID id, @Valid UserEditRequest userEditRequest) {
        User user = getById(id);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setProfilePicture(userEditRequest.getProfilePicture());

        userRepository.save(user);
    }
}
