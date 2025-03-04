package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.books.service.BookLikesService;
import lifeplanner.books.service.BookService;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.service.GoalLikesService;
import lifeplanner.goals.service.GoalService;
import lifeplanner.media.model.Media;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.media.service.MediaLikesService;
import lifeplanner.media.service.MediaService;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeLikesService;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.service.TravelService;
import lifeplanner.travel.service.TripLikesService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.LoginRequest;
import lifeplanner.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final TravelService travelService;
    private final TripLikesService tripLikesService;
    private final GoalService goalService;
    private final GoalLikesService goalLikesService;

    @Autowired
    public IndexController(UserService userService, BookService bookService, BookLikesService bookLikesService, BookFavoriteService bookFavoriteService, MediaService mediaService, MediaLikesService mediaLikesService, MediaFavoriteService mediaFavoriteService, RecipeService recipeService, RecipeLikesService recipeLikesService, TravelService travelService, TripLikesService tripLikesService, GoalService goalService, GoalLikesService goalLikesService) {
        this.userService = userService;
        this.bookService = bookService;
        this.bookLikesService = bookLikesService;
        this.bookFavoriteService = bookFavoriteService;
        this.mediaService = mediaService;
        this.mediaLikesService = mediaLikesService;
        this.mediaFavoriteService = mediaFavoriteService;
        this.recipeService = recipeService;
        this.recipeLikesService = recipeLikesService;
        this.travelService = travelService;
        this.tripLikesService = tripLikesService;
        this.goalService = goalService;
        this.goalLikesService = goalLikesService;
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
    public ModelAndView getLoginPage(Model model) {
        model.addAttribute("pageTitle", "Login");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

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

    @PostMapping("/login")
    public String loginUser(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session, Model model) {
        model.addAttribute("pageTitle", "Login");

        if (bindingResult.hasErrors()) {
            return "login";
        }

        User user = userService.loginUser(loginRequest);
        session.setAttribute("user_id", user.getId());

        return "redirect:/home";

    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session, Model model) {
        model.addAttribute("pageTitle", "Home");
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        if (userId == null) {
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

        // TRIPS
        List<Travel> allTrips = travelService.getAllTrips();
        List<Travel> sharedTrips = travelService.getSharedTrips(user);

        // For each trip, retrieve its like count
        Map<UUID, Long> tripLikeCounts = new HashMap<>();
        for (Travel t : sharedTrips) {
            long count = tripLikesService.getLikeCount(t.getId());
            tripLikeCounts.put(t.getId(), count);
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
        // TRIPS
        modelAndView.addObject("allTrips", allTrips);
        modelAndView.addObject("sharedTrips", sharedTrips);
        model.addAttribute("tripLikeCounts", tripLikeCounts);
        // GOALS
        modelAndView.addObject("allGoals", allGoals);
        modelAndView.addObject("sharedGoals", sharedGoals);
        model.addAttribute("goalLikeCounts", goalLikeCounts);


        return modelAndView;
    }

    @GetMapping("/favorites")
    public String getFavoritesPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "LifeHub Favorites");
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        // BOOKS
        List<Book> favoriteBooks = bookFavoriteService.getFavoritesByUser(user);
        model.addAttribute("favoriteBooks", favoriteBooks);

        // Media
        List<Media> favoriteMedia = mediaFavoriteService.getFavoritesByUser(user);
        model.addAttribute("favoriteMedia", favoriteMedia);

        model.addAttribute("user", user);
        return "favorites";
    }

    @GetMapping("/pending-approval")
    public String getPendingApprovalPage(Model model) {
        model.addAttribute("pageTitle", "Pending Approval");
        return "pending-approval";
    }

    @GetMapping("/all-users")
    public String getAllUsersPage(Model model) {
        model.addAttribute("pageTitle", "All Users");
        return "all-users";
    }

    @GetMapping("/logout")
    public String getLogoutPage(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }
}