package lifeplanner.web.books;

import lifeplanner.books.service.BookFavoriteService;
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

@WebMvcTest(BookFavoriteController.class)
class BookFavoriteControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookFavoriteService bookFavoriteService;

    // Add the missing dependency.
    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID testUserId = UUID.randomUUID();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(testUserId, "user", "password", UserRole.USER, true);
    }

    @Test
    void toggleBookFavorite_ShouldReturnJsonResponse() throws Exception {
        UUID bookId = UUID.randomUUID();

        when(bookFavoriteService.toggleFavorite(bookId, testUserId)).thenReturn(true);
        when(bookFavoriteService.getFavoriteCount(bookId)).thenReturn(5L);

        mockMvc.perform(post("/api/books/{bookId}/favorite", bookId)
                        .with(user(getAuth()))
                        .with(csrf())) // CSRF token is added here
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.favorited").value(true))
                .andExpect(jsonPath("$.favoriteCount").value(5));
    }
}