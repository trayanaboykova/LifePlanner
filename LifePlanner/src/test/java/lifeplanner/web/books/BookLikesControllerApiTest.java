package lifeplanner.web.books;

import lifeplanner.books.service.BookLikesService;
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

@WebMvcTest(BookLikesController.class)
class BookLikesControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookLikesService bookLikesService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID testUserId = UUID.randomUUID();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(testUserId, "user", "password", UserRole.USER, true);
    }

    @Test
    void toggleBookLike_ShouldReturnJsonResponse() throws Exception {
        UUID bookId = UUID.randomUUID();

        when(bookLikesService.toggleLike(bookId, testUserId)).thenReturn(true);
        when(bookLikesService.getLikeCount(bookId)).thenReturn(10L);

        mockMvc.perform(post("/api/books/{bookId}/like", bookId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(10));
    }
}