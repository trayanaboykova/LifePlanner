package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BooksController {

    @GetMapping("/my-books")
    public String getMyBooksPage(Model model) {
        model.addAttribute("pageTitle", "My Books");
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

    @GetMapping("/add-books")
    public String getAddBooksPage(Model model) {
        model.addAttribute("pageTitle", "Add Book");
        return "add-books";
    }

    @GetMapping("/edit-book")
    public String getEditBooksPage(Model model) {
        model.addAttribute("pageTitle", "Edit Book");
        return "edit-book";
    }
}
