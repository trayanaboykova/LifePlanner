package lifeplanner.web.media;

import jakarta.servlet.http.HttpSession;
import lifeplanner.media.service.MediaFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaFavoriteController {

    private final MediaFavoriteService mediaFavoriteService;

    @Autowired
    public MediaFavoriteController(MediaFavoriteService mediaFavoriteService) {
        this.mediaFavoriteService = mediaFavoriteService;
    }

    @PostMapping("/{mediaId}/favorite")
    public Map<String, Object> toggleMediaFavorite(@PathVariable UUID mediaId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isFavorited = mediaFavoriteService.toggleFavoriteMedia(mediaId, userId);
        long newCount = mediaFavoriteService.getFavoriteCount(mediaId);

        return Map.of(
                "favorited", isFavorited,
                "favoriteCount", newCount
        );
    }
}