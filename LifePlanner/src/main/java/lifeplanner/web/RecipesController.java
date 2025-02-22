package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecipesController {

    @GetMapping("/all-recipes")
    public String getAllRecipesPage(Model model) {
        model.addAttribute("pageTitle", "All Recipes");
        return "all-recipes";
    }

    @GetMapping("/add-recipe")
    public String getAddRecipePage(Model model) {
        model.addAttribute("pageTitle", "Add Recipe");
        return "add-recipe";
    }

    @GetMapping("/edit-recipe")
    public String getEditRecipePage(Model model) {
        model.addAttribute("pageTitle", "Edit Recipe");
        return "edit-recipe";
    }
}
