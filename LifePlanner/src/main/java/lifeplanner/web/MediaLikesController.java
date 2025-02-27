package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import lifeplanner.media.service.MediaLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaLikesController {
    private final MediaLikesService mediaLikesService;

    @Autowired
    public MediaLikesController(MediaLikesService mediaLikesService) {
        this.mediaLikesService = mediaLikesService;
    }

    @PostMapping("/{mediaId}/like")
    public Map<String, Object> toggleMediaLike(@PathVariable UUID mediaId,
                                          HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isLiked = mediaLikesService.toggleLike(mediaId, userId);
        long newCount = mediaLikesService.getLikeCount(mediaId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }


}
