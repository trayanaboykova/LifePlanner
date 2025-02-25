package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import lifeplanner.books.service.BookLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookLikesController {
    private final BookLikesService booklikesService;

    @Autowired
    public BookLikesController(BookLikesService booklikesService) {
        this.booklikesService = booklikesService;
    }


    @PostMapping("/{bookId}/like")
    public Map<String, Object> toggleLike(@PathVariable UUID bookId,
                                          HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isLiked = booklikesService.toggleLike(bookId, userId);
        long newCount = booklikesService.getLikeCount(bookId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }
}
