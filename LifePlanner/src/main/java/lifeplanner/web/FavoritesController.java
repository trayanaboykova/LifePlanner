package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/favorites")
public class FavoritesController {

    private final BookFavoriteService bookFavoriteService;
    private final MediaFavoriteService mediaFavoriteService;
    private final RecipeFavoriteService recipeFavoriteService;
    private final TripFavoriteService tripFavoriteService;
    private final GoalFavoriteService goalFavoriteService;
    private final UserService userService;

    @Autowired
    public FavoritesController(BookFavoriteService bookFavoriteService,
                               MediaFavoriteService mediaFavoriteService,
                               RecipeFavoriteService recipeFavoriteService,
                               TripFavoriteService tripFavoriteService,
                               GoalFavoriteService goalFavoriteService,
                               UserService userService) {
        this.bookFavoriteService = bookFavoriteService;
        this.mediaFavoriteService = mediaFavoriteService;
        this.recipeFavoriteService = recipeFavoriteService;
        this.tripFavoriteService = tripFavoriteService;
        this.goalFavoriteService = goalFavoriteService;
        this.userService = userService;
    }

    @PostMapping("/books/{bookId}/remove")
    public String removeBookFavorite(@PathVariable UUID bookId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);
        bookFavoriteService.removeFavorite(user, bookId);
        return "redirect:/favorites";
    }

    @PostMapping("/media/{mediaId}/remove")
    public String removeMediaFavorite(@PathVariable UUID mediaId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);
        mediaFavoriteService.removeFavorite(user, mediaId);
        return "redirect:/favorites";
    }

    @PostMapping("/recipes/{recipeId}/remove")
    public String removeRecipeFavorite(@PathVariable UUID recipeId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);
        recipeFavoriteService.removeFavorite(user, recipeId);
        return "redirect:/favorites";
    }

    @PostMapping("/trips/{tripId}/remove")
    public String removeTripFavorite(@PathVariable UUID tripId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);
        tripFavoriteService.removeFavorite(user, tripId);
        return "redirect:/favorites";
    }

    @PostMapping("/goals/{goalId}/remove")
    public String removeGoalFavorite(@PathVariable UUID goalId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);
        goalFavoriteService.removeFavorite(user, goalId);
        return "redirect:/favorites";
    }
}