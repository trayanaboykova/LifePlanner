package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
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
    public String registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerUser(registerRequest);

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        User user = userService.loginUser(loginRequest);
        session.setAttribute("user_id", user.getId());

        return "redirect:/home";

    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        model.addAttribute("pageTitle", "Home");
        return "home";
    }

    @GetMapping("/logout")
    public String getLogoutPage(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

}
