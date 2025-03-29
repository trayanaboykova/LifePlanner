package lifeplanner.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lifeplanner.validation.ValidPasswordChange;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@ValidPasswordChange
public class UserEditRequest {

    @Size(max = 20, message = "First name length must be 20 characters max!")
    private String firstName;

    @Size(max = 20, message = "Last name length must be 20 characters max!")
    private String lastName;

    @Email(message = "Enter valid email address")
    private String email;

    @URL(message = "Enter valid URL link")
    private String profilePicture;

    private boolean removeProfilePic;

    private transient MultipartFile profilePictureFile;

    private String currentPassword;

    private String newPassword;

    private String confirmNewPassword;

}