package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/lifehub-fav")
    public String getLifeHubFavoritesPage(Model model) {
        model.addAttribute("pageTitle", "LifeHub Favorites");
        return "lifehub-fav";
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
}
