package lifeplanner.web.media;

import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaStatus;
import lifeplanner.media.service.MediaService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import lifeplanner.web.dto.AddMediaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
public class MediaControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @MockitoBean
    private UserService userService;

    // Required for security.
    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final UUID mediaId = UUID.randomUUID();
    private final User testUser = new User();
    private final Media testMedia = new Media();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(userId, "user", "password", UserRole.USER, true);
    }

    @Test
    void getAllMediaPage_ShouldReturnAllMediaView() throws Exception {
        testUser.setId(userId);
        testMedia.setId(mediaId);
        // (Status not required for "all-media")
        when(userService.getById(userId)).thenReturn(testUser);
        when(mediaService.getMediaByUser(testUser)).thenReturn(Arrays.asList(testMedia));

        mockMvc.perform(get("/media/all-media").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-media"))
                .andExpect(model().attribute("pageTitle", "All Media"))
                .andExpect(model().attribute("media", Arrays.asList(testMedia)));
    }

    @Test
    void getWatchedPage_ShouldReturnWatchedView() throws Exception {
        testUser.setId(userId);
        testMedia.setId(mediaId);
        // Set a non-null status so that the template can call media.status.name()
        testMedia.setStatus(MediaStatus.WATCHED);
        when(userService.getById(userId)).thenReturn(testUser);
        when(mediaService.getMediaByUser(testUser)).thenReturn(Arrays.asList(testMedia));

        mockMvc.perform(get("/media/watched").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("watched"))
                .andExpect(model().attribute("pageTitle", "Watched"))
                .andExpect(model().attribute("media", Arrays.asList(testMedia)));
    }

    @Test
    void getWatchlistPage_ShouldReturnWatchlistView() throws Exception {
        testUser.setId(userId);
        testMedia.setId(mediaId);
        // Set a non-null status for the watchlist view
        testMedia.setStatus(MediaStatus.WANT_TO_WATCH);
        when(userService.getById(userId)).thenReturn(testUser);
        when(mediaService.getMediaByUser(testUser)).thenReturn(Arrays.asList(testMedia));

        mockMvc.perform(get("/media/watchlist").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("watchlist"))
                .andExpect(model().attribute("pageTitle", "Watchlist"))
                .andExpect(model().attribute("media", Arrays.asList(testMedia)));
    }

    @Test
    void showAddMediaRequest_ShouldReturnAddMediaView() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/media/new").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-media"))
                .andExpect(model().attributeExists("pageTitle", "user", "addMediaRequest"));
    }

    @Test
    void addMedia_WithValidRequest_ShouldRedirect() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/media")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("status", "WATCHED")
                        .param("type", "MOVIE")
                        .param("title", "Test Media")
                        .param("director", "Test Director"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/media/all-media"));

        verify(mediaService).addMedia(any(AddMediaRequest.class), eq(testUser));
    }

    @Test
    void addMedia_WithInvalidRequest_ShouldReturnAddMediaForm() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/media")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("status", "WATCHED")
                        .param("type", "MOVIE")
                        .param("director", "Test Director"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-media"));
    }

    @Test
    void showEditMediaRequest_ShouldReturnEditMediaView() throws Exception {
        testMedia.setId(mediaId);
        when(mediaService.getMediaById(mediaId)).thenReturn(testMedia);

        mockMvc.perform(get("/media/{id}/edit", mediaId).with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-media"))
                .andExpect(model().attributeExists("pageTitle", "media", "editMediaRequest"));
    }

    @Test
    void updateMedia_WithValidRequest_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/media/{id}/edit", mediaId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("status", "WATCHED")
                        .param("type", "MOVIE")
                        .param("title", "Updated Title")
                        .param("director", "Updated Director"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/media/all-media"));
    }

    @Test
    void updateMedia_WithBindingErrors_ShouldReturnEditMediaView() throws Exception {
        testMedia.setId(mediaId);
        when(mediaService.getMediaById(mediaId)).thenReturn(testMedia);

        // Passing an empty "title" to trigger a binding error.
        mockMvc.perform(post("/media/{id}/edit", mediaId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("status", "WATCHED")
                        .param("type", "MOVIE")
                        .param("title", "")
                        .param("director", "Some Director"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-media"))
                .andExpect(model().attributeExists("media", "editMediaRequest"));
    }

    @Test
    void shareMedia_ShouldRedirectToAllMedia() throws Exception {
        mockMvc.perform(post("/media/{id}/share", mediaId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/media/all-media"));
    }

    @Test
    void removeSharing_ShouldRedirectToMySharedPosts() throws Exception {
        mockMvc.perform(post("/media/{id}/remove", mediaId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-shared-posts"));
    }

    @Test
    void deleteMedia_ShouldRedirectToAllMedia() throws Exception {
        mockMvc.perform(delete("/media/{id}", mediaId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/media/all-media"));
    }
}