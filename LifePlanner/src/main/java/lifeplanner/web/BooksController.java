package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.books.repository.BookRepository;
import lifeplanner.books.service.BookService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public BooksController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/my-books")
    public String getMyBooksPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "My Books");
        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Book> userBooks = bookService.getBooksByUser(user);

        model.addAttribute("books", userBooks);

        return "my-books";
    }

    @GetMapping("/all-books")
    public String getAllBooksPage(Model model) {
        model.addAttribute("pageTitle", "All Books");
        return "all-books";
    }

    @GetMapping("/read-books")
    public String getAllReadPage(Model model) {
        model.addAttribute("pageTitle", "Read Books");
        return "read-books";
    }

    @GetMapping("/wished-books")
    public String getWishListPage(Model model) {
        model.addAttribute("pageTitle", "Wish List");
        return "wished-books";
    }

    @GetMapping("/new")
    public ModelAndView showAddBookRequest(HttpSession session, Model model) {

        model.addAttribute("pageTitle", "Add Book");

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-books");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addBookRequest", new AddBookRequest());

        return modelAndView;
    }

    @PostMapping
    public String addBook(@Valid AddBookRequest addBookRequest, BindingResult bindingResult, HttpSession session, Model model) {

        model.addAttribute("pageTitle", "Add Book");

        if (bindingResult.hasErrors()) {
            return "add-books";
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        bookService.addBook(addBookRequest, user);

        return "redirect:/home";
    }


    @GetMapping("/edit-book")
    public String getEditBooksPage(Model model) {
        model.addAttribute("pageTitle", "Edit Book");
        return "edit-book";
    }
}
