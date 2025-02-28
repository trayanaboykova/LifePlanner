package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.books.service.BookService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddBookRequest;
import lifeplanner.web.dto.EditBookRequest;
import lifeplanner.web.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

        model.addAttribute("user", user);
        model.addAttribute("books", userBooks);

        return "my-books";
    }

    @GetMapping("/all-books")
    public String getAllBooksPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "All Books");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Book> userBooks = bookService.getBooksByUser(user);

        model.addAttribute("books", userBooks);

        return "all-books";
    }

    @GetMapping("/read-books")
    public String getAllReadPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Read Books");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Book> userBooks = bookService.getBooksByUser(user);

        model.addAttribute("books", userBooks);

        return "read-books";
    }

    @GetMapping("/wished-books")
    public String getWishListPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Wish List");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Book> userBooks = bookService.getBooksByUser(user);

        model.addAttribute("books", userBooks);

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
    public String addBook(@Valid AddBookRequest addBookRequest,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model) {

        model.addAttribute("pageTitle", "Add Book");

        if (bindingResult.hasErrors()) {
            return "add-books";
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        bookService.addBook(addBookRequest, user);

        return "redirect:/books/all-books";
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditBookRequest(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("pageTitle", "Edit Book");

        Book book = bookService.getBookById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-book");
        modelAndView.addObject("book", book);
        modelAndView.addObject("editBookRequest", DTOMapper.mapBookToEditBookRequest(book));
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateBook(@PathVariable("id") UUID id,
                                   @Valid @ModelAttribute("editBookRequest") EditBookRequest editBookRequest,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Book book = bookService.getBookById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-book");
            modelAndView.addObject("book", book);
            modelAndView.addObject("editBookRequest", editBookRequest);
            return modelAndView;
        }

        bookService.editBook(id, editBookRequest);
        return new ModelAndView("redirect:/books/all-books");
    }

    @PostMapping("/{id}/share")
    public String shareBook(@PathVariable UUID id) {

        bookService.shareBook(id);

        return "redirect:/books/my-books";
    }


    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable UUID id) {

        bookService.deleteBookById(id);

        return "redirect:/books/my-books";
    }

}
