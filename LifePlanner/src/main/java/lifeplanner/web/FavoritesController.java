package lifeplanner.web;

import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String removeBookFavorite(@PathVariable UUID bookId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        UUID userId = authenticationMetadata.getUserId();
        bookFavoriteService.removeFavorite(userService.getById(userId), bookId);
        return "redirect:/favorites";
    }

    @PostMapping("/media/{mediaId}/remove")
    public String removeMediaFavorite(@PathVariable UUID mediaId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        UUID userId = authenticationMetadata.getUserId();
        mediaFavoriteService.removeFavorite(userService.getById(userId), mediaId);
        return "redirect:/favorites";
    }

    @PostMapping("/recipes/{recipeId}/remove")
    public String removeRecipeFavorite(@PathVariable UUID recipeId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        UUID userId = authenticationMetadata.getUserId();
        recipeFavoriteService.removeFavorite(userService.getById(userId), recipeId);
        return "redirect:/favorites";
    }

    @PostMapping("/trips/{tripId}/remove")
    public String removeTripFavorite(@PathVariable UUID tripId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        UUID userId = authenticationMetadata.getUserId();
        tripFavoriteService.removeFavorite(userService.getById(userId), tripId);
        return "redirect:/favorites";
    }

    @PostMapping("/goals/{goalId}/remove")
    public String removeGoalFavorite(@PathVariable UUID goalId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        UUID userId = authenticationMetadata.getUserId();
        goalFavoriteService.removeFavorite(userService.getById(userId), goalId);
        return "redirect:/favorites";
    }
}