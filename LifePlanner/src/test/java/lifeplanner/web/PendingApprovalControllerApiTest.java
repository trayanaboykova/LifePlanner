package lifeplanner.web;

import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookService;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.service.GoalService;
import lifeplanner.media.model.Media;
import lifeplanner.media.service.MediaService;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.service.TravelService;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PendingApprovalController.class)
public class PendingApprovalControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private MediaService mediaService;

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private TravelService travelService;

    @MockitoBean
    private GoalService goalService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final User testUser = new User();

    // Helper method to simulate an admin user.
    private AuthenticationMetadata getAdminAuth() {
        return new AuthenticationMetadata(userId, "admin", "password", UserRole.ADMIN, true);
    }

    @Test
    void getPendingApprovalPage_ShouldReturnPendingApprovalView_WhenUserExists() throws Exception {
        // Set up the admin user
        testUser.setId(userId);

        // Create dummy pending Book with a non-null owner.
        User bookOwner = new User();
        bookOwner.setUsername("bookOwner");
        Book dummyBook = new Book();
        dummyBook.setOwner(bookOwner);

        // Create dummy pending Media with a non-null owner.
        User mediaOwner = new User();
        mediaOwner.setUsername("mediaOwner");
        Media dummyMedia = new Media();
        dummyMedia.setOwner(mediaOwner);

        // Create dummy pending Recipe with a non-null owner.
        User recipeOwner = new User();
        recipeOwner.setUsername("recipeOwner");
        Recipe dummyRecipe = new Recipe();
        dummyRecipe.setOwner(recipeOwner);

        // Create dummy pending Travel with a non-null owner.
        User travelOwner = new User();
        travelOwner.setUsername("travelOwner");
        Travel dummyTravel = new Travel();
        dummyTravel.setOwner(travelOwner);

        // Create dummy pending Goal with a non-null owner.
        User goalOwner = new User();
        goalOwner.setUsername("goalOwner");
        Goal dummyGoal = new Goal();
        dummyGoal.setOwner(goalOwner);

        // Configure the mocks so that the services return these dummy items.
        when(userService.getById(userId)).thenReturn(testUser);
        when(bookService.getPendingBooks()).thenReturn(Arrays.asList(dummyBook));
        when(mediaService.getPendingMedia()).thenReturn(Arrays.asList(dummyMedia));
        when(recipeService.getPendingRecipes()).thenReturn(Arrays.asList(dummyRecipe));
        when(travelService.getPendingTravel()).thenReturn(Arrays.asList(dummyTravel));
        when(goalService.getPendingGoals()).thenReturn(Arrays.asList(dummyGoal));

        // Perform the GET request and verify that the model contains the pending items.
        mockMvc.perform(get("/pending-approval").with(user(getAdminAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("pending-approval"))
                .andExpect(model().attribute("pageTitle", "Pending Approval"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("pendingBooks", Arrays.asList(dummyBook)))
                .andExpect(model().attribute("pendingMedia", Arrays.asList(dummyMedia)))
                .andExpect(model().attribute("pendingRecipes", Arrays.asList(dummyRecipe)))
                .andExpect(model().attribute("pendingTravel", Arrays.asList(dummyTravel)))
                .andExpect(model().attribute("pendingGoals", Arrays.asList(dummyGoal)));
    }

    @Test
    void getPendingApprovalPage_ShouldRedirectToLogin_WhenUserIsNull() throws Exception {
        when(userService.getById(userId)).thenReturn(null);

        mockMvc.perform(get("/pending-approval").with(user(getAdminAuth())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void handleBulkApproval_ShouldApproveItems_WhenActionIsApprove() throws Exception {
        // Arrange: generate dummy UUIDs.
        UUID bookId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        UUID recipeId = UUID.randomUUID();
        UUID travelId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();

        List<String> selectedItems = Arrays.asList(
                "BOOK-" + bookId,
                "MEDIA-" + mediaId,
                "RECIPE-" + recipeId,
                "TRAVEL-" + travelId,
                "GOAL-" + goalId
        );

        mockMvc.perform(post("/approve-selected")
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        .param("action", "approve")
                        .param("selectedItems", selectedItems.toArray(new String[0])))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pending-approval"));

        verify(bookService).approveBook(eq(bookId));
        verify(mediaService).approveMedia(eq(mediaId));
        verify(recipeService).approveRecipe(eq(recipeId));
        verify(travelService).approveTrip(eq(travelId));
        verify(goalService).approveGoal(eq(goalId));
    }

    @Test
    void handleBulkApproval_ShouldRejectItems_WhenActionIsReject() throws Exception {
        // Arrange: generate dummy UUIDs.
        UUID bookId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        UUID recipeId = UUID.randomUUID();
        UUID travelId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();

        List<String> selectedItems = Arrays.asList(
                "BOOK-" + bookId,
                "MEDIA-" + mediaId,
                "RECIPE-" + recipeId,
                "TRAVEL-" + travelId,
                "GOAL-" + goalId
        );

        mockMvc.perform(post("/approve-selected")
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        .param("action", "reject")
                        .param("selectedItems", selectedItems.toArray(new String[0])))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pending-approval"));

        verify(bookService).rejectBook(eq(bookId));
        verify(mediaService).rejectMedia(eq(mediaId));
        verify(recipeService).rejectRecipe(eq(recipeId));
        verify(travelService).rejectTrip(eq(travelId));
        verify(goalService).rejectGoal(eq(goalId));
    }

    @Test
    void handleBulkApproval_ShouldRedirect_WhenNoSelectedItems() throws Exception {
        mockMvc.perform(post("/approve-selected")
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        .param("action", "approve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pending-approval"));
    }

    @Test
    void handleBulkApproval_ShouldHandleUnknownItemType_WhenItemTypeIsUnknown() throws Exception {
        UUID unknownId = UUID.randomUUID();
        List<String> selectedItems = Arrays.asList("UNKNOWN-" + unknownId);

        mockMvc.perform(post("/approve-selected")
                        .with(user(getAdminAuth()))
                        .with(csrf())
                        .param("action", "approve")
                        .param("selectedItems", selectedItems.toArray(new String[0])))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pending-approval"));

        verifyNoInteractions(bookService, mediaService, recipeService, travelService, goalService);
    }
}