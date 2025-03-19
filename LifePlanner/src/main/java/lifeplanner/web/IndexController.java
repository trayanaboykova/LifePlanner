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
import lifeplanner.scheduler.DailyQuotesScheduler;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;
    private final BookService bookService;
    private final BookLikesService bookLikesService;
    private final BookFavoriteService bookFavoriteService;
    private final MediaService mediaService;
    private final MediaLikesService mediaLikesService;
    private final MediaFavoriteService mediaFavoriteService;
    private final RecipeService recipeService;
    private final RecipeLikesService recipeLikesService;
    private final RecipeFavoriteService recipeFavoriteService;
    private final TravelService travelService;
    private final TripLikesService tripLikesService;
    private final TripFavoriteService tripFavoriteService;
    private final GoalService goalService;
    private final GoalLikesService goalLikesService;
    private final GoalFavoriteService goalFavoriteService;
    private final DailyQuotesScheduler dailyQuoteScheduler;
    private final DateAndTimeScheduler dateAndTimeScheduler;


    @Autowired
    public IndexController(UserService userService, BookService bookService, BookLikesService bookLikesService, BookFavoriteService bookFavoriteService, MediaService mediaService, MediaLikesService mediaLikesService, MediaFavoriteService mediaFavoriteService, RecipeService recipeService, RecipeLikesService recipeLikesService, RecipeFavoriteService recipeFavoriteService, TravelService travelService, TripLikesService tripLikesService, TripFavoriteService tripFavoriteService, GoalService goalService, GoalLikesService goalLikesService, GoalFavoriteService goalFavoriteService, DailyQuotesScheduler dailyQuoteScheduler, DateAndTimeScheduler dateAndTimeScheduler) {
        this.userService = userService;
        this.bookService = bookService;
        this.bookLikesService = bookLikesService;
        this.bookFavoriteService = bookFavoriteService;
        this.mediaService = mediaService;
        this.mediaLikesService = mediaLikesService;
        this.mediaFavoriteService = mediaFavoriteService;
        this.recipeService = recipeService;
        this.recipeLikesService = recipeLikesService;
        this.recipeFavoriteService = recipeFavoriteService;
        this.travelService = travelService;
        this.tripLikesService = tripLikesService;
        this.tripFavoriteService = tripFavoriteService;
        this.goalService = goalService;
        this.goalLikesService = goalLikesService;
        this.goalFavoriteService = goalFavoriteService;
        this.dailyQuoteScheduler = dailyQuoteScheduler;
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
        List<Book> sharedBooks = bookService.getSharedBooks(user);

        // For each book, retrieve its like count
        Map<UUID, Long> bookLikeCounts = new HashMap<>();
        for (Book b : sharedBooks) {
            long count = bookLikesService.getLikeCount(b.getId());
            bookLikeCounts.put(b.getId(), count);
        }
        // For each book, retrieve its favorite count
        Map<UUID, Long> bookFavoriteCounts = new HashMap<>();
        for (Book b : sharedBooks) {
            long favoriteCount = bookFavoriteService.getFavoriteCount(b.getId());
            bookFavoriteCounts.put(b.getId(), favoriteCount);
        }

        // MEDIA
        List<Media> allMedia = mediaService.getAllMedia();
        List<Media> sharedMedia = mediaService.getSharedMedia(user);

        // For each book, retrieve its like count
        Map<UUID, Long> mediaLikeCounts = new HashMap<>();
        for (Media m : sharedMedia) {
            long count = mediaLikesService.getLikeCount(m.getId());
            mediaLikeCounts.put(m.getId(), count);
        }

        // For each book, retrieve its favorite count
        Map<UUID, Long> mediaFavoriteCounts = new HashMap<>();
        for (Media m : sharedMedia) {
            long favoriteCount = mediaFavoriteService.getFavoriteCount(m.getId());
            mediaFavoriteCounts.put(m.getId(), favoriteCount);
        }

        // RECIPES
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        List<Recipe> sharedRecipes = recipeService.getSharedRecipes(user);

        // For each recipe, retrieve its like count
        Map<UUID, Long> recipeLikeCounts = new HashMap<>();
        for (Recipe r : sharedRecipes) {
            long count = recipeLikesService.getLikeCount(r.getId());
            recipeLikeCounts.put(r.getId(), count);
        }

        // For each recipe, retrieve its favorite count
        Map<UUID, Long> recipeFavoriteCounts = new HashMap<>();
        for (Recipe r : sharedRecipes) {
            long count = recipeFavoriteService.getFavoriteCount(r.getId());
            recipeFavoriteCounts.put(r.getId(), count);
        }

        // TRIPS
        List<Travel> allTrips = travelService.getAllTrips();
        List<Travel> sharedTrips = travelService.getSharedTrips(user);

        // For each trip, retrieve its like count
        Map<UUID, Long> tripLikeCounts = new HashMap<>();
        for (Travel t : sharedTrips) {
            long count = tripLikesService.getLikeCount(t.getId());
            tripLikeCounts.put(t.getId(), count);
        }

        // For each trip, retrieve its favorite count
        Map<UUID, Long> tripFavoriteCounts = new HashMap<>();
        for (Travel t : sharedTrips) {
            long count = tripFavoriteService.getFavoriteCount(t.getId());
            tripFavoriteCounts.put(t.getId(), count);
        }

        // GOALS
        List<Goal> allGoals = goalService.getAllGoals();
        List<Goal> sharedGoals = goalService.getSharedGoals(user);

        // For each goal, retrieve its like count
        Map<UUID, Long> goalLikeCounts = new HashMap<>();
        for (Goal g : sharedGoals) {
            long count = goalLikesService.getLikeCount(g.getId());
            goalLikeCounts.put(g.getId(), count);
        }

        // For each goal, retrieve its favorite count
        Map<UUID, Long> goalFavoriteCounts = new HashMap<>();
        for (Goal g : sharedGoals) {
            long count = goalFavoriteService.getFavoriteCount(g.getId());
            goalFavoriteCounts.put(g.getId(), count);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        // BOOKS
        modelAndView.addObject("allBooks", allBooks);
        modelAndView.addObject("sharedBooks", sharedBooks);
        model.addAttribute("bookLikeCounts", bookLikeCounts);
        model.addAttribute("bookFavoriteCounts", bookFavoriteCounts);
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
}