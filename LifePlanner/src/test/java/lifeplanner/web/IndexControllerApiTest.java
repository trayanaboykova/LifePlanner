package lifeplanner.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookFavoriteService;
import lifeplanner.books.service.BookService;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalStatus;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.goals.service.GoalService;
import lifeplanner.media.model.Media;
import lifeplanner.media.service.MediaFavoriteService;
import lifeplanner.media.service.MediaService;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.scheduler.DateAndTimeScheduler;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.travel.service.TravelService;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private BookFavoriteService bookFavoriteService;
    @MockitoBean
    private MediaService mediaService;
    @MockitoBean
    private MediaFavoriteService mediaFavoriteService;
    @MockitoBean
    private RecipeService recipeService;
    @MockitoBean
    private RecipeFavoriteService recipeFavoriteService;
    @MockitoBean
    private TravelService travelService;
    @MockitoBean
    private TripFavoriteService tripFavoriteService;
    @MockitoBean
    private GoalService goalService;
    @MockitoBean
    private GoalFavoriteService goalFavoriteService;
    @MockitoBean
    private DateAndTimeScheduler dateAndTimeScheduler;
    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    // Helper to simulate an authenticated user.
    private AuthenticationMetadata getAdminAuth() {
        return new AuthenticationMetadata(UUID.randomUUID(), "admin", "password", UserRole.ADMIN, true);
    }

    @Test
    void getIndexPage_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("pageTitle", "LifePlanner"));
    }

    @Test
    void getRegisterPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("pageTitle", "Register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getLoginPage_ShouldReturnLoginView_WithErrorMessage() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("pageTitle", "Login"))
                .andExpect(model().attribute("errorMessage", "Incorrect username or password!"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getLoginPage_ShouldReturnLoginView_NoError() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("pageTitle", "Login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attributeDoesNotExist("errorMessage"));
    }

    @Test
    void registerNewUser_ShouldRedirectToLogin_WhenValid() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "johnDoe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password123")
                        .param("confirmPassword", "Password123")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).registerUser(org.mockito.ArgumentMatchers.argThat(req ->
                "johnDoe".equals(req.getUsername()) &&
                        "john.doe@example.com".equals(req.getEmail()) &&
                        "Password123".equals(req.getPassword()) &&
                        "Password123".equals(req.getConfirmPassword())
        ));
    }

    @Test
    void registerNewUser_ShouldReturnRegisterView_WhenBindingErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "invalid-email")
                        .param("password", "Password123")
                        .param("confirmPassword", "Password123")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("pageTitle", "Register"));
    }

    @Test
    void getHomePage_ShouldReturnHomeView_WithAllAttributes() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authMeta = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        User user = new User();
        user.setId(userId);
        user.setUsername("user");

        when(userService.getById(userId)).thenReturn(user);

        // Create a dummy owner for all shared objects.
        User dummyOwner = new User();
        dummyOwner.setUsername("dummyOwner");

        // BOOKS
        Book book1 = new Book();
        book1.setId(UUID.randomUUID());
        book1.setOwner(dummyOwner);
        Book book2 = new Book();
        book2.setId(UUID.randomUUID());
        book2.setOwner(dummyOwner);
        List<Book> allBooks = Arrays.asList(book1, book2);

        Book sharedBook = new Book();
        sharedBook.setId(UUID.randomUUID());
        sharedBook.setOwner(dummyOwner);
        List<Book> sharedBooks = Arrays.asList(sharedBook);

        Map<UUID, Long> bookLikeCounts = Map.of(sharedBook.getId(), 1L);
        Map<UUID, Long> bookFavoriteCounts = Map.of(sharedBook.getId(), 2L);
        when(bookService.getAllBooks()).thenReturn(allBooks);
        when(bookService.getApprovedSharedBooks(user)).thenReturn(sharedBooks);
        when(bookService.getLikeCountsForBooks(sharedBooks)).thenReturn(bookLikeCounts);
        when(bookService.getFavoriteCountsForBooks(sharedBooks)).thenReturn(bookFavoriteCounts);

        // MEDIA
        Media media1 = new Media();
        media1.setId(UUID.randomUUID());
        List<Media> allMedia = Arrays.asList(media1);

        Media media2 = new Media();
        media2.setId(UUID.randomUUID());
        media2.setOwner(dummyOwner);
        Media media3 = new Media();
        media3.setId(UUID.randomUUID());
        media3.setOwner(dummyOwner);
        List<Media> sharedMedia = Arrays.asList(media2, media3);

        Map<UUID, Long> mediaLikeCounts = Map.of(media2.getId(), 3L, media3.getId(), 4L);
        Map<UUID, Long> mediaFavoriteCounts = Map.of(media2.getId(), 5L, media3.getId(), 6L);
        when(mediaService.getAllMedia()).thenReturn(allMedia);
        when(mediaService.getApprovedSharedMedia(user)).thenReturn(sharedMedia);
        when(mediaService.getLikeCountsForMedia(sharedMedia)).thenReturn(mediaLikeCounts);
        when(mediaService.getFavoriteCountsForMedia(sharedMedia)).thenReturn(mediaFavoriteCounts);

        // RECIPES
        Recipe recipe1 = new Recipe();
        recipe1.setId(UUID.randomUUID());
        List<Recipe> allRecipes = Arrays.asList(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setId(UUID.randomUUID());
        recipe2.setOwner(dummyOwner);
        Recipe recipe3 = new Recipe();
        recipe3.setId(UUID.randomUUID());
        recipe3.setOwner(dummyOwner);
        List<Recipe> sharedRecipes = Arrays.asList(recipe2, recipe3);

        Map<UUID, Long> recipeLikeCounts = Map.of(recipe2.getId(), 7L, recipe3.getId(), 8L);
        Map<UUID, Long> recipeFavoriteCounts = Map.of(recipe2.getId(), 9L, recipe3.getId(), 10L);
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        when(recipeService.getApprovedSharedRecipes(user)).thenReturn(sharedRecipes);
        when(recipeService.getLikeCountsForRecipes(allRecipes)).thenReturn(recipeLikeCounts);
        when(recipeService.getFavoriteCountsForRecipes(sharedRecipes)).thenReturn(recipeFavoriteCounts);

        // TRIPS
        Travel travel1 = new Travel();
        travel1.setId(UUID.randomUUID());
        travel1.setOwner(dummyOwner);
        List<Travel> allTrips = Arrays.asList(travel1);

        Travel travel2 = new Travel();
        travel2.setId(UUID.randomUUID());
        travel2.setOwner(dummyOwner);
        Travel travel3 = new Travel();
        travel3.setId(UUID.randomUUID());
        travel3.setOwner(dummyOwner);
        List<Travel> sharedTrips = Arrays.asList(travel2, travel3);

        Map<UUID, Long> tripLikeCounts = Map.of(travel2.getId(), 11L, travel3.getId(), 12L);
        Map<UUID, Long> tripFavoriteCounts = Map.of(travel2.getId(), 13L, travel3.getId(), 14L);
        when(travelService.getAllTrips()).thenReturn(allTrips);
        when(travelService.getApprovedSharedTrips(user)).thenReturn(sharedTrips);
        when(travelService.getLikeCountsForTrips(sharedTrips)).thenReturn(tripLikeCounts);
        when(travelService.getFavoriteCountsForTrips(sharedTrips)).thenReturn(tripFavoriteCounts);

        // GOALS
        Goal goal1 = new Goal();
        goal1.setId(UUID.randomUUID());
        goal1.setOwner(dummyOwner);
        goal1.setStatus(GoalStatus.NOT_STARTED);
        List<Goal> allGoals = Arrays.asList(goal1);

        Goal goal2 = new Goal();
        goal2.setId(UUID.randomUUID());
        goal2.setOwner(dummyOwner);
        goal2.setStatus(GoalStatus.IN_PROGRESS);
        Goal goal3 = new Goal();
        goal3.setId(UUID.randomUUID());
        goal3.setOwner(dummyOwner);
        goal3.setStatus(GoalStatus.COMPLETED);
        List<Goal> sharedGoals = Arrays.asList(goal2, goal3);

        Map<UUID, Long> goalLikeCounts = Map.of(goal2.getId(), 15L, goal3.getId(), 16L);
        Map<UUID, Long> goalFavoriteCounts = Map.of(goal2.getId(), 17L, goal3.getId(), 18L);
        when(goalService.getAllGoals()).thenReturn(allGoals);
        when(goalService.getApprovedSharedGoals(user)).thenReturn(sharedGoals);
        when(goalService.getLikeCountsForGoals(allGoals)).thenReturn(goalLikeCounts);
        when(goalService.getFavoriteCountsForGoals(sharedGoals)).thenReturn(goalFavoriteCounts);

        // Scheduler: the controller returns the LocalDateTime.toString() which produces "2025-03-27T00:00"
        String expectedCurrentDateTime = "2025-03-27T00:00";
        when(dateAndTimeScheduler.getCurrentDateTime())
                .thenReturn(LocalDateTime.parse("2025-03-27T00:00:00"));

        mockMvc.perform(get("/home").with(user(authMeta)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("pageTitle", "Home"))
                .andExpect(model().attribute("user", user))
                // Books
                .andExpect(model().attribute("allBooks", allBooks))
                .andExpect(model().attribute("sharedBooks", sharedBooks))
                .andExpect(model().attribute("bookLikeCounts", bookLikeCounts))
                .andExpect(model().attribute("bookFavoriteCounts", bookFavoriteCounts))
                // Media
                .andExpect(model().attribute("allMedia", allMedia))
                .andExpect(model().attribute("sharedMedia", sharedMedia))
                .andExpect(model().attribute("mediaLikeCounts", mediaLikeCounts))
                .andExpect(model().attribute("mediaFavoriteCounts", mediaFavoriteCounts))
                // Recipes
                .andExpect(model().attribute("allRecipes", allRecipes))
                .andExpect(model().attribute("sharedRecipes", sharedRecipes))
                .andExpect(model().attribute("recipeLikeCounts", recipeLikeCounts))
                .andExpect(model().attribute("recipeFavoriteCounts", recipeFavoriteCounts))
                // Trips
                .andExpect(model().attribute("allTrips", allTrips))
                .andExpect(model().attribute("sharedTrips", sharedTrips))
                .andExpect(model().attribute("tripLikeCounts", tripLikeCounts))
                .andExpect(model().attribute("tripFavoriteCounts", tripFavoriteCounts))
                // Goals
                .andExpect(model().attribute("allGoals", allGoals))
                .andExpect(model().attribute("sharedGoals", sharedGoals))
                .andExpect(model().attribute("goalLikeCounts", goalLikeCounts))
                .andExpect(model().attribute("goalFavoriteCounts", goalFavoriteCounts))
                // Scheduler: Use hasToString to convert LocalDateTime to string and compare ignoring whitespace.
                .andExpect(model().attribute("currentDateTime",
                        org.hamcrest.Matchers.hasToString(org.hamcrest.Matchers.equalToIgnoringWhiteSpace(expectedCurrentDateTime))));
    }

    @Test
    void getHomePage_ShouldRedirectToLogin_WhenUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authMeta = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);

        when(userService.getById(userId)).thenReturn(null);

        mockMvc.perform(get("/home").with(user(authMeta)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    void getFavoritesPage_ShouldReturnFavoritesView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authMeta = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        User user = new User();
        user.setId(userId);
        user.setUsername("user");

        when(userService.getById(userId)).thenReturn(user);

        List<Book> favoriteBooks = Arrays.asList(new Book());
        List<Media> favoriteMedia = Arrays.asList(new Media());
        List<Recipe> favoriteRecipe = Arrays.asList(new Recipe());
        List<Travel> favoriteTrip = Arrays.asList(new Travel());
        List<Goal> favoriteGoal = Arrays.asList(new Goal());

        when(bookFavoriteService.getFavoritesByUser(user)).thenReturn(favoriteBooks);
        when(mediaFavoriteService.getFavoritesByUser(user)).thenReturn(favoriteMedia);
        when(recipeFavoriteService.getFavoritesByUser(user)).thenReturn(favoriteRecipe);
        when(tripFavoriteService.getFavoritesByUser(user)).thenReturn(favoriteTrip);
        when(goalFavoriteService.getFavoritesByUser(user)).thenReturn(favoriteGoal);

        mockMvc.perform(get("/favorites").with(user(authMeta)))
                .andExpect(status().isOk())
                .andExpect(view().name("favorites"))
                .andExpect(model().attribute("favoriteBooks", favoriteBooks))
                .andExpect(model().attribute("favoriteMedia", favoriteMedia))
                .andExpect(model().attribute("favoriteRecipe", favoriteRecipe))
                .andExpect(model().attribute("favoriteTrip", favoriteTrip))
                .andExpect(model().attribute("favoriteGoal", favoriteGoal))
                .andExpect(model().attribute("user", user));
    }

    @Test
    void getMySharedPostsPage_ShouldReturnMySharedPostsView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authMeta = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        User user = new User();
        user.setId(userId);
        user.setUsername("user");

        when(userService.getById(userId)).thenReturn(user);

        // Create a dummy owner for shared posts.
        User dummyOwner = new User();
        dummyOwner.setUsername("dummyOwner");

        // BOOKS
        Book sharedBook = new Book();
        sharedBook.setId(UUID.randomUUID());
        sharedBook.setOwner(dummyOwner);
        List<Book> sharedBooks = Arrays.asList(sharedBook);

        // MEDIA
        Media sharedMediaItem = new Media();
        sharedMediaItem.setId(UUID.randomUUID());
        sharedMediaItem.setOwner(dummyOwner);
        List<Media> sharedMedia = Arrays.asList(sharedMediaItem);

        // RECIPES
        Recipe sharedRecipe = new Recipe();
        sharedRecipe.setId(UUID.randomUUID());
        sharedRecipe.setOwner(dummyOwner);
        List<Recipe> sharedRecipes = Arrays.asList(sharedRecipe);

        // TRIPS
        Travel sharedTrip = new Travel();
        sharedTrip.setId(UUID.randomUUID());
        sharedTrip.setOwner(dummyOwner);
        List<Travel> sharedTrips = Arrays.asList(sharedTrip);

        // GOALS - Create a goal with a dummy owner.
        Goal sharedGoal = new Goal();
        sharedGoal.setId(UUID.randomUUID());
        sharedGoal.setOwner(dummyOwner);
        List<Goal> sharedGoals = Arrays.asList(sharedGoal);

        when(bookService.getMySharedBooks(user)).thenReturn(sharedBooks);
        when(mediaService.getMySharedMedia(user)).thenReturn(sharedMedia);
        when(recipeService.getMySharedRecipes(user)).thenReturn(sharedRecipes);
        when(travelService.getMySharedTrips(user)).thenReturn(sharedTrips);
        when(goalService.getMySharedGoals(user)).thenReturn(sharedGoals);

        mockMvc.perform(get("/my-shared-posts").with(user(authMeta)))
                .andExpect(status().isOk())
                .andExpect(view().name("my-shared-posts"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("sharedBooks", sharedBooks))
                .andExpect(model().attribute("sharedMedia", sharedMedia))
                .andExpect(model().attribute("sharedRecipes", sharedRecipes))
                .andExpect(model().attribute("sharedTrips", sharedTrips))
                .andExpect(model().attribute("sharedGoals", sharedGoals));
    }
}