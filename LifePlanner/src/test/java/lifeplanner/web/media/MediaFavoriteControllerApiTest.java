package lifeplanner.web.media;

import lifeplanner.media.service.MediaFavoriteService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaFavoriteController.class)
class MediaFavoriteControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaFavoriteService mediaFavoriteService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID testUserId = UUID.randomUUID();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(testUserId, "user", "password", UserRole.USER, true);
    }

    @Test
    void toggleMediaFavorite_ShouldReturnJsonResponse() throws Exception {
        UUID mediaId = UUID.randomUUID();
        when(mediaFavoriteService.toggleFavoriteMedia(mediaId, testUserId)).thenReturn(true);
        when(mediaFavoriteService.getFavoriteCount(mediaId)).thenReturn(10L);

        mockMvc.perform(post("/api/media/{mediaId}/favorite", mediaId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.favorited").value(true))
                .andExpect(jsonPath("$.favoriteCount").value(10));
    }
}