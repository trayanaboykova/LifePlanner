package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("pageTitle", "LifePlanner");
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "register";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("pageTitle", "Home");
        return "home";
    }

}
