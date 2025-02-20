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
}
