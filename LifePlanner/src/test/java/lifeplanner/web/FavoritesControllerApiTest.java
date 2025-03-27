package lifeplanner.web;

import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
public class FavoritesControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookFavoriteService bookFavoriteService;

    @MockitoBean
    private MediaFavoriteService mediaFavoriteService;

    @MockitoBean
    private RecipeFavoriteService recipeFavoriteService;

    @MockitoBean
    private TripFavoriteService tripFavoriteService;

    @MockitoBean
    private GoalFavoriteService goalFavoriteService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final User testUser = new User();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(userId, "user", "password", UserRole.USER, true);
    }

    @Test
    void removeBookFavorite_ShouldRedirectToFavorites() throws Exception {
        UUID bookId = UUID.randomUUID();
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/favorites/books/{bookId}/remove", bookId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/favorites"));

        verify(bookFavoriteService).removeFavorite(eq(testUser), eq(bookId));
    }

    @Test
    void removeMediaFavorite_ShouldRedirectToFavorites() throws Exception {
        UUID mediaId = UUID.randomUUID();
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/favorites/media/{mediaId}/remove", mediaId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/favorites"));

        verify(mediaFavoriteService).removeFavorite(eq(testUser), eq(mediaId));
    }

    @Test
    void removeRecipeFavorite_ShouldRedirectToFavorites() throws Exception {
        UUID recipeId = UUID.randomUUID();
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/favorites/recipes/{recipeId}/remove", recipeId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/favorites"));

        verify(recipeFavoriteService).removeFavorite(eq(testUser), eq(recipeId));
    }

    @Test
    void removeTripFavorite_ShouldRedirectToFavorites() throws Exception {
        UUID tripId = UUID.randomUUID();
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/favorites/trips/{tripId}/remove", tripId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/favorites"));

        verify(tripFavoriteService).removeFavorite(eq(testUser), eq(tripId));
    }

    @Test
    void removeGoalFavorite_ShouldRedirectToFavorites() throws Exception {
        UUID goalId = UUID.randomUUID();
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/favorites/goals/{goalId}/remove", goalId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/favorites"));

        verify(goalFavoriteService).removeFavorite(eq(testUser), eq(goalId));
    }
}