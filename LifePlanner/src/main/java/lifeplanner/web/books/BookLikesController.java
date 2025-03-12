package lifeplanner.web.books;

import lifeplanner.books.service.BookLikesService;
import lifeplanner.security.AuthenticationMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookLikesController {
    private final BookLikesService bookLikesService;

    @Autowired
    public BookLikesController(BookLikesService booklikesService) {
        this.bookLikesService = booklikesService;
    }

    @PostMapping("/{bookId}/like")
    public Map<String, Object> toggleBookLike(@PathVariable UUID bookId,
                                              @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        boolean isLiked = bookLikesService.toggleLike(bookId, userId);
        long newCount = bookLikesService.getLikeCount(bookId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }
}