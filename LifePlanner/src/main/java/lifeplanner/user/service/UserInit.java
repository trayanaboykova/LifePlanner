package lifeplanner.user.service;

import lifeplanner.user.model.UserRole;
import lifeplanner.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!userService.getAllUsers().isEmpty()){
            return;
        }

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(adminUsername)
                .password(adminPassword)
                .confirmPassword(adminPassword)
                .role(UserRole.ADMIN)
                .build();

        userService.registerUser(registerRequest);
    }
}