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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
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
    void updateUserProfile_WithBindingErrors_ShouldReturnEditProfileView() throws Exception {
        UUID userId = UUID.randomUUID();
        // Stub user lookup so the controller can add the user to the model.
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        when(userService.getById(userId)).thenReturn(user);

        // Simulate a binding error by sending an empty firstName (assuming it’s @NotBlank)
        mockMvc.perform(put("/users/{id}/profile", userId)
                        .param("firstName", "") // this should trigger a validation error
                        .param("lastName", "Doe")
                        .param("email", "john@example.com")
                        .with(user(getAdminAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("user"))
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