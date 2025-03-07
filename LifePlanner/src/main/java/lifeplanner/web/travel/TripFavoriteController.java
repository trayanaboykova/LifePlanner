package lifeplanner.web.travel;

import jakarta.servlet.http.HttpSession;
import lifeplanner.travel.service.TripFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
public class TripFavoriteController {

    private final TripFavoriteService tripFavoriteService;

    @Autowired
    public TripFavoriteController(TripFavoriteService tripFavoriteService) {
        this.tripFavoriteService = tripFavoriteService;
    }

    @PostMapping("/{tripId}/favorite")
    public Map<String, Object> toggleTripFavorite(@PathVariable UUID tripId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        boolean isFavorited = tripFavoriteService.toggleFavoriteTrip(tripId, userId);
        long newCount = tripFavoriteService.getFavoriteCount(tripId);

        return Map.of(
                "favorited", isFavorited,
                "favoriteCount", newCount
        );
    }
}