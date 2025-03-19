package lifeplanner.web;

import lifeplanner.books.service.BookService;
import lifeplanner.goals.service.GoalService;
import lifeplanner.media.service.MediaService;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.service.TravelService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class PendingApprovalController {
    private final UserService userService;
    private final BookService bookService;
    private final MediaService mediaService;
    private final RecipeService recipeService;
    private final TravelService travelService;
    private final GoalService goalService;

    @Autowired
    public PendingApprovalController(UserService userService, BookService bookService, MediaService mediaService, RecipeService recipeService, TravelService travelService, GoalService goalService) {
        this.userService = userService;
        this.bookService = bookService;
        this.mediaService = mediaService;
        this.recipeService = recipeService;
        this.travelService = travelService;
        this.goalService = goalService;
    }

    @GetMapping("/pending-approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getPendingApprovalPage(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "Pending Approval");

        User user = userService.getById(authenticationMetadata.getUserId());

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        model.addAttribute("pendingBooks", bookService.getPendingBooks());
        model.addAttribute("pendingMedia", mediaService.getPendingMedia());
        model.addAttribute("pendingRecipes", recipeService.getPendingRecipes());
        model.addAttribute("pendingTravel", travelService.getPendingTravel());
        model.addAttribute("pendingGoals", goalService.getPendingGoals());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pending-approval");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/approve-selected")
    @PreAuthorize("hasRole('ADMIN')")
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
                // Handle Travel approvals
                else if (item.startsWith("TRAVEL-")) {
                    String idStr = item.substring("TRAVEL-".length());
                    UUID id = UUID.fromString(idStr);
                    if ("approve".equalsIgnoreCase(action)) {
                        travelService.approveTrip(id);
                    } else if ("reject".equalsIgnoreCase(action)) {
                        travelService.rejectTrip(id);
                    }
                }
                // Handle Goal approvals
                else if (item.startsWith("GOAL-")) {
                    String idStr = item.substring("GOAL-".length());
                    UUID id = UUID.fromString(idStr);
                    if ("approve".equalsIgnoreCase(action)) {
                        goalService.approveGoal(id);
                    } else if ("reject".equalsIgnoreCase(action)) {
                        goalService.rejectGoal(id);
                    }
                }
            }
        }
        return "redirect:/pending-approval";
    }
}