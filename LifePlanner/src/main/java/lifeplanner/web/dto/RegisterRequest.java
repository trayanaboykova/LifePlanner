package lifeplanner.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RegisterRequest {

    @NotNull(message = "Username must not be empty.")
    @Size(min = 4, max = 20, message = "Username length must be between 4 and 20 characters!")
    private String username;

    @NotNull(message = "Email must not be empty.")
    @Email(message = "Not a valid email format")
    private String email;

    @NotNull(message = "Password must not be empty.")
    @Size(min = 4, max = 20, message = "Password length must be between 4 and 20 characters!")
    private String password;

    @NotNull(message = "Password must not be empty.")
    @Size(min = 4, max = 20, message = "Password length must be between 4 and 20 characters!")
    private String confirmPassword;

    private UserRole role;

}