package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MediaController {

    @GetMapping("/all-media")
    public String getAllMediaPage(Model model) {
        model.addAttribute("pageTitle", "All Media");
        return "all-media";
    }

    @GetMapping("/watched")
    public String getWatchedPage(Model model) {
        model.addAttribute("pageTitle", "Watched");
        return "watched";
    }

    @GetMapping("/watchlist")
    public String getWatchlistPage(Model model) {
        model.addAttribute("pageTitle", "Watchlist");
        return "watchlist";
    }

    @GetMapping("/add-media")
    public String getAddMediaPage(Model model) {
        model.addAttribute("pageTitle", "Add Media");
        return "add-media";
    }

    @GetMapping("/edit-media")
    public String getEditMediaPage(Model model) {
        model.addAttribute("pageTitle", "Edit Media");
        return "edit-media";
    }
}
