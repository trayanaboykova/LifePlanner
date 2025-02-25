package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookLikesService;
import lifeplanner.books.service.BookService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.LoginRequest;
import lifeplanner.web.dto.RegisterRequest;
import org.apache.tomcat.util.codec.binary.Base64;
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

    @Autowired
    public IndexController(UserService userService, BookService bookService, BookLikesService bookLikesService) {
        this.userService = userService;
        this.bookService = bookService;
        this.bookLikesService = bookLikesService;
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

        List<Book> allBooks = bookService.getAllBooks();
        List<Book> sharedBooks = bookService.getSharedBooks(user);

        // For each book, retrieve its like count
        Map<UUID, Long> likeCounts = new HashMap<>();
        for (Book b : sharedBooks) {
            long count = bookLikesService.getLikeCount(b.getId());
            likeCounts.put(b.getId(), count);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allBooks", allBooks);
        modelAndView.addObject("sharedBooks", sharedBooks);
        model.addAttribute("likeCounts", likeCounts);

        return modelAndView;
    }

    @GetMapping("/logout")
    public String getLogoutPage(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

}
