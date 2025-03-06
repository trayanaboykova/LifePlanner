package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import lifeplanner.books.service.BookService;
import lifeplanner.goals.service.GoalService;
import lifeplanner.media.service.MediaService;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.travel.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class PendingApprovalController {
    private final BookService bookService;
    private final MediaService mediaService;
    private final RecipeService recipeService;
    private final TravelService travelService;
    private final GoalService goalService;

    @Autowired
    public PendingApprovalController(BookService bookService, MediaService mediaService, RecipeService recipeService, TravelService travelService, GoalService goalService) {
        this.bookService = bookService;
        this.mediaService = mediaService;
        this.recipeService = recipeService;
        this.travelService = travelService;
        this.goalService = goalService;
    }

    @GetMapping("/pending-approval")
    public String getPendingApprovalPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Pending Approval");

        model.addAttribute("pendingBooks", bookService.getPendingBooks());
        model.addAttribute("pendingMedia", mediaService.getPendingMedia());
        model.addAttribute("pendingRecipes", recipeService.getPendingRecipes());
        model.addAttribute("pendingTravel", travelService.getPendingTravel());
        model.addAttribute("pendingGoals", goalService.getPendingGoals());

        return "pending-approval";
    }

    @PostMapping("/approve-selected")
    public String handleBulkApproval(
            @RequestParam(name = "selectedItems", required = false) List<String> selectedItems,
            @RequestParam("action") String action) {

        if (selectedItems != null) {
            for (String item : selectedItems) {
                // Handle Book approvals
                if (item.startsWith("BOOK-")) {
                    String idStr = item.substring("BOOK-".length());
                    UUID id = UUID.fromString(idStr);
                    if ("approve".equalsIgnoreCase(action)) {
                        bookService.approveBook(id);
                    } else if ("reject".equalsIgnoreCase(action)) {
                        bookService.rejectBook(id);
                    }
                }
                // Handle Media approvals
                if (item.startsWith("MEDIA-")) {
                    String idStr = item.substring("MEDIA-".length());
                    UUID id = UUID.fromString(idStr);
                    if ("approve".equalsIgnoreCase(action)) {
                        mediaService.approveMedia(id);
                    } else if ("reject".equalsIgnoreCase(action)) {
                        mediaService.rejectMedia(id);
                    }
                }
                // Handle Recipe approvals
                else if (item.startsWith("RECIPE-")) {
                    String idStr = item.substring("RECIPE-".length());
                    UUID id = UUID.fromString(idStr);
                    if ("approve".equalsIgnoreCase(action)) {
                        recipeService.approveRecipe(id);
                    } else if ("reject".equalsIgnoreCase(action)) {
                        recipeService.rejectRecipe(id);
                    }
                }
            }
        }
        return "redirect:/pending-approval";
    }
}