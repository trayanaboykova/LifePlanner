package lifeplanner.web.mapper;

import lifeplanner.user.model.User;
import lifeplanner.web.dto.UserEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DTOMapper {
    public static UserEditRequest mapUserToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
