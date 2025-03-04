package lifeplanner.web.books;

import jakarta.servlet.http.HttpSession;
import lifeplanner.books.service.BookFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookFavoriteController {

    private final BookFavoriteService favoriteService;

    @Autowired
    public BookFavoriteController(BookFavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{bookId}/favorite")
    public Map<String, Object> toggleBookFavorite(@PathVariable UUID bookId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isFavorited = favoriteService.toggleFavorite(bookId, userId);
        long newCount = favoriteService.getFavoriteCount(bookId);

        return Map.of(
                "favorited", isFavorited,
                "favoriteCount", newCount
        );
    }
}
