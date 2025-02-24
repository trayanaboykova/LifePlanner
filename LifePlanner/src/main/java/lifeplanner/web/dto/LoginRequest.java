package lifeplanner.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull(message = "Username must not be empty.")
    @Size(min = 4, max = 20, message = "Username length must be between 4 and 20 characters!")
    private String username;

    @NotNull(message = "Password must not be empty.")
    @Size(min = 4, max = 20, message = "Password length must be between 4 and 20 characters!")
    private String password;

}
