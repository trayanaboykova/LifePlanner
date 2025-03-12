package lifeplanner.web.books;

import lifeplanner.books.service.BookFavoriteService;
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
public class BookFavoriteController {

    private final BookFavoriteService bookFavoriteService;

    @Autowired
    public BookFavoriteController(BookFavoriteService favoriteService) {
        this.bookFavoriteService = favoriteService;
    }

    @PostMapping("/{bookId}/favorite")
    public Map<String, Object> toggleBookFavorite(@PathVariable UUID bookId,
                                                  @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        boolean isFavorited = bookFavoriteService.toggleFavorite(bookId, userId);
        long newCount = bookFavoriteService.getFavoriteCount(bookId);

        // Return JSON with new state & count
        return Map.of(
                "favorited", isFavorited,
                "favoriteCount", newCount
        );
    }
}