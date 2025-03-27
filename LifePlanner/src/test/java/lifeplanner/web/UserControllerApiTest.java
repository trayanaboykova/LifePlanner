package lifeplanner.web;

import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.service.CloudinaryService;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private AuthenticationMetadata getAdminAuth() {
        return new AuthenticationMetadata(UUID.randomUUID(), "admin", "password", UserRole.ADMIN, true);
    }

    @Test
    void getProfileMenu_ShouldReturnEditProfileView() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}/profile", userId)
                        .with(user(getAdminAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("userEditRequest"))
                .andExpect(model().attribute("pageTitle", "Edit Profile"));
    }

    @Test
    void updateUserProfile_ShouldUpdateProfile_WithFileUpload() throws Exception {
        UUID userId = UUID.randomUUID();
        String uploadedUrl = "http://cloudinary.com/image.jpg";

        when(cloudinaryService.uploadFile(org.mockito.ArgumentMatchers.any())).thenReturn(uploadedUrl);

        MockMultipartFile file = new MockMultipartFile(
                "profilePictureFile", "test.jpg", "image/jpeg", "dummy image content".getBytes()
        );

        mockMvc.perform(multipart("/users/{id}/profile", userId)
                        // Set the method to PUT
                        .file(file)
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        .with(request -> { request.setMethod("PUT"); return request; })
                        // Set form fields (adjust field names as per your UserEditRequest requirements)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("removeProfilePic", "false")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("successMessage", "Profile updated successfully!"));

        verify(userService).editUserDetails(eq(userId), org.mockito.ArgumentMatchers.argThat(userEdit ->
                uploadedUrl.equals(userEdit.getProfilePicture())
        ));
    }

    @Test
    void updateUserProfile_ShouldUpdateProfile_WhenRemoveProfilePicIsTrue() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/users/{id}/profile", userId)
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        // Provide required fields.
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("removeProfilePic", "true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("successMessage", "Profile updated successfully!"));

        verify(userService).editUserDetails(eq(userId), org.mockito.ArgumentMatchers.argThat(userEdit ->
                userEdit.getProfilePicture() == null
        ));
    }

    @Test
    void updateUserProfile_ShouldReturnEditProfileView_WhenBindingErrors() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userService.getById(userId)).thenReturn(user);

        String invalidFirstName = "ThisFirstNameIsWayTooLongToBeValid";

        mockMvc.perform(put("/users/{id}/profile", userId)
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        .param("firstName", invalidFirstName)
                        .param("lastName", "Doe")
                        .param("removeProfilePic", "false")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("userEditRequest"));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsersView() throws Exception {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setRole(UserRole.USER); // Set role to avoid null

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setRole(UserRole.USER); // Set role as well

        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users/all-users").with(user(getAdminAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-users"))
                .andExpect(model().attribute("users", users));
    }

    @Test
    void deactivateUserProfile_ShouldRedirectToLogout() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{id}/deactivate", userId)
                        .with(user(getAdminAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));

        verify(userService).deactivateUserProfile(eq(userId));
    }

    @Test
    void switchUserRole_ShouldSwitchRoleAndRedirect() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/users/{id}/role", userId)
                        .with(user(getAdminAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all-users"))
                .andExpect(flash().attribute("successMessage", "User role updated successfully!"));

        verify(userService).switchRole(eq(userId));
    }

    @Test
    void switchUserStatus_ShouldSwitchStatusAndRedirect() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/users/{id}/status", userId)
                        .with(user(getAdminAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all-users"))
                .andExpect(flash().attribute("successMessage", "User status updated successfully!"));

        verify(userService).switchStatus(eq(userId));
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRedirect() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{id}", userId)
                        .with(user(getAdminAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all-users"))
                .andExpect(flash().attribute("successMessage", "User deleted successfully!"));

        verify(userService).deleteUserById(eq(userId));
    }
}