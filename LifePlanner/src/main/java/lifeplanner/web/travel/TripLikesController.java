package lifeplanner.web.travel;

import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.service.TripLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/trips")
public class TripLikesController {
    private final TripLikesService tripLikesService;

    @Autowired
    public TripLikesController(TripLikesService tripLikesService) {
        this.tripLikesService = tripLikesService;
    }

    @PostMapping("/{tripId}/like")
    public Map<String, Object> toggleTripLike(@PathVariable UUID tripId,
                                              @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        boolean isLiked = tripLikesService.toggleLike(tripId, userId);
        long newCount = tripLikesService.getLikeCount(tripId);

        // Return JSON with new state & count
        return Map.of(
                "liked", isLiked,
                "likeCount", newCount
        );
    }
}