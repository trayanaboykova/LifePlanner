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

    @GetMapping("/edit-user")
    public String getEditUserPage(Model model) {
        model.addAttribute("pageTitle", "Edit Profile");
        return "edit-user";
    }
}
