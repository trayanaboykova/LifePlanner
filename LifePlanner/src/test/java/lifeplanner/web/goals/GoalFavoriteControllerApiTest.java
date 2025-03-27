package lifeplanner.web.goals;

import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.UserRole;
import lifeplanner.validation.CustomAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalFavoriteController.class)
public class GoalFavoriteControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalFavoriteService goalFavoriteService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID testUserId = UUID.randomUUID();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(testUserId, "user", "password", UserRole.USER, true);
    }

    @Test
    void toggleGoalFavorite_ShouldReturnJsonResponse() throws Exception {
        UUID goalId = UUID.randomUUID();

        when(goalFavoriteService.toggleFavorite(goalId, testUserId)).thenReturn(true);
        when(goalFavoriteService.getFavoriteCount(goalId)).thenReturn(3L);

        mockMvc.perform(post("/api/goals/{goalId}/favorite", goalId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.favorited").value(true))
                .andExpect(jsonPath("$.favoriteCount").value(3));
    }
}
