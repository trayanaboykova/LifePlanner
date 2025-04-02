package lifeplanner.web;

import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.books.service.BookLikesService;
import lifeplanner.books.service.BookService;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.goals.service.GoalLikesService;
import lifeplanner.goals.service.GoalService;
import lifeplanner.media.model.Media;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.media.service.MediaLikesService;
import lifeplanner.media.service.MediaService;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.recipes.service.RecipeLikesService;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.scheduler.DateAndTimeScheduler;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.service.TravelService;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.travel.service.TripLikesService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.LoginRequest;
import lifeplanner.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;
    private final BookService bookService;
    private final BookFavoriteService bookFavoriteService;
    private final MediaService mediaService;
    private final MediaFavoriteService mediaFavoriteService;
    private final RecipeService recipeService;
    private final RecipeFavoriteService recipeFavoriteService;
    private final TravelService travelService;
    private final TripFavoriteService tripFavoriteService;
    private final GoalService goalService;
    private final GoalFavoriteService goalFavoriteService;
    private final DateAndTimeScheduler dateAndTimeScheduler;


    @Autowired
    public IndexController(UserService userService,
                           BookService bookService,
                           BookFavoriteService bookFavoriteService,
                           MediaService mediaService,
                           MediaFavoriteService mediaFavoriteService,
                           RecipeService recipeService,
                           RecipeFavoriteService recipeFavoriteService,
                           TravelService travelService,
                           TripFavoriteService tripFavoriteService,
                           GoalService goalService,
                           GoalFavoriteService goalFavoriteService,
                           DateAndTimeScheduler dateAndTimeScheduler) {
        this.userService = userService;
        this.bookService = bookService;
        this.bookFavoriteService = bookFavoriteService;
        this.mediaService = mediaService;
        this.mediaFavoriteService = mediaFavoriteService;
        this.recipeService = recipeService;
        this.recipeFavoriteService = recipeFavoriteService;
        this.travelService = travelService;
        this.tripFavoriteService = tripFavoriteService;
        this.goalService = goalService;
        this.goalFavoriteService = goalFavoriteService;
        this.dateAndTimeScheduler = dateAndTimeScheduler;
    }

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("pageTitle", "LifePlanner");
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(Model model) {
        model.addAttribute("pageTitle", "Register");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam, Model model) {
        model.addAttribute("pageTitle", "Login");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }

        return modelAndView;
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("pageTitle", "Register");

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerUser(registerRequest);

        return "redirect:/login";
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, Model model) {
        model.addAttribute("pageTitle", "Home");
        User user = userService.getById(authenticationMetadata.getUserId());

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        // BOOKS
        List<Book> allBooks = bookService.getAllBooks();
        List<Book> sharedBooks = bookService.getApprovedSharedBooks(user);
        Map<UUID, Long> bookLikeCounts = bookService.getLikeCountsForBooks(sharedBooks);
        Map<UUID, Long> bookFavoriteCounts = bookService.getFavoriteCountsForBooks(sharedBooks);

        // MEDIA
        List<Media> allMedia = mediaService.getAllMedia();
        List<Media> sharedMedia = mediaService.getApprovedSharedMedia(user);
        Map<UUID, Long> mediaLikeCounts = mediaService.getLikeCountsForMedia(sharedMedia);
        Map<UUID, Long> mediaFavoriteCounts = mediaService.getFavoriteCountsForMedia(sharedMedia);

        // RECIPES
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        List<Recipe> sharedRecipes = recipeService.getApprovedSharedRecipes(user);
        Map<UUID, Long> recipeLikeCounts = recipeService.getLikeCountsForRecipes(allRecipes);
        Map<UUID, Long> recipeFavoriteCounts = recipeService.getFavoriteCountsForRecipes(sharedRecipes);

        // TRIPS
        List<Travel> allTrips = travelService.getAllTrips();
        List<Travel> sharedTrips = travelService.getApprovedSharedTrips(user);
        Map<UUID, Long> tripLikeCounts = travelService.getLikeCountsForTrips(sharedTrips);
        Map<UUID, Long> tripFavoriteCounts = travelService.getFavoriteCountsForTrips(sharedTrips);

        // GOALS
        List<Goal> allGoals = goalService.getAllGoals();
        List<Goal> sharedGoals = goalService.getApprovedSharedGoals(user);
        Map<UUID, Long> goalLikeCounts = goalService.getLikeCountsForGoals(allGoals);
        Map<UUID, Long> goalFavoriteCounts = goalService.getFavoriteCountsForGoals(sharedGoals);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        // BOOKS
        modelAndView.addObject("allBooks", allBooks);
        modelAndView.addObject("sharedBooks", sharedBooks);
        model.addAttribute("bookLikeCounts", bookLikeCounts);
        model.addAttribute("bookFavoriteCounts",bookFavoriteCounts);
        // MEDIA
        modelAndView.addObject("allMedia", allMedia);
        modelAndView.addObject("sharedMedia", sharedMedia);
        model.addAttribute("mediaLikeCounts", mediaLikeCounts);
        model.addAttribute("mediaFavoriteCounts", mediaFavoriteCounts);
        // RECIPES
        modelAndView.addObject("allRecipes", allRecipes);
        modelAndView.addObject("sharedRecipes", sharedRecipes);
        model.addAttribute("recipeLikeCounts", recipeLikeCounts);
        model.addAttribute("recipeFavoriteCounts", recipeFavoriteCounts);
        // TRIPS
        modelAndView.addObject("allTrips", allTrips);
        modelAndView.addObject("sharedTrips", sharedTrips);
        model.addAttribute("tripLikeCounts", tripLikeCounts);
        model.addAttribute("tripFavoriteCounts", tripFavoriteCounts);
        // GOALS
        modelAndView.addObject("allGoals", allGoals);
        modelAndView.addObject("sharedGoals", sharedGoals);
        model.addAttribute("goalLikeCounts", goalLikeCounts);
        model.addAttribute("goalFavoriteCounts", goalFavoriteCounts);

        // Schedulers
        model.addAttribute("currentDateTime", dateAndTimeScheduler.getCurrentDateTime());

        return modelAndView;
    }

    @GetMapping("/favorites")
    public String getFavoritesPage(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "LifeHub Favorites");

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Book> favoriteBooks = bookFavoriteService.getFavoritesByUser(user);
        List<Media> favoriteMedia = mediaFavoriteService.getFavoritesByUser(user);
        List<Recipe> favoriteRecipe = recipeFavoriteService.getFavoritesByUser(user);
        List<Travel> favoriteTrip = tripFavoriteService.getFavoritesByUser(user);
        List<Goal> favoriteGoal = goalFavoriteService.getFavoritesByUser(user);

        model.addAttribute("favoriteBooks", favoriteBooks);
        model.addAttribute("favoriteMedia", favoriteMedia);
        model.addAttribute("favoriteRecipe", favoriteRecipe);
        model.addAttribute("favoriteTrip", favoriteTrip);
        model.addAttribute("favoriteGoal", favoriteGoal);
        model.addAttribute("user", user);
        return "favorites";
    }

    @GetMapping("/my-shared-posts")
    public String getMySharedPostsPage(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "My Shared Posts");

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Book> sharedBooks = bookService.getMySharedBooks(user);
        List<Media> sharedMedia = mediaService.getMySharedMedia(user);
        List<Recipe> sharedRecipes = recipeService.getMySharedRecipes(user);
        List<Travel> sharedTrips = travelService.getMySharedTrips(user);
        List<Goal> sharedGoals = goalService.getMySharedGoals(user);

        model.addAttribute("user", user);
        model.addAttribute("sharedBooks", sharedBooks);
        model.addAttribute("sharedMedia", sharedMedia);
        model.addAttribute("sharedRecipes", sharedRecipes);
        model.addAttribute("sharedTrips", sharedTrips);
        model.addAttribute("sharedGoals", sharedGoals);

        return "my-shared-posts";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About Us");
        return "about";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("pageTitle", "Privacy Policy");
        return "privacy";
    }

    @GetMapping("/terms-and-conditions")
    public String terms(Model model) {
        model.addAttribute("pageTitle", "Terms and Conditions");
        return "terms-and-conditions";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact");
        return "contact";
    }

    @GetMapping("/faqs")
    public String faq(Model model) {
        model.addAttribute("pageTitle", "FAQ");
        return "faqs";
    }

}