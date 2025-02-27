package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddBookRequest;
import lifeplanner.web.dto.AddRecipeRequest;
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
@RequestMapping("recipes")
public class RecipesController {

    private final RecipeService recipeService;
    private final UserService userService;

    @Autowired
    public RecipesController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @GetMapping("/all-recipes")
    public String getAllRecipesPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "All Recipes");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Recipe> userRecipe = recipeService.getRecipeByUser(user);

        model.addAttribute("recipes", userRecipe);

        return "all-recipes";
    }

    @GetMapping("/new")
    public ModelAndView showAddRecipeRequest(HttpSession session, Model model) {

        model.addAttribute("pageTitle", "Add Recipe");

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-recipe");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addRecipeRequest", new AddRecipeRequest());

        return modelAndView;
    }

    @PostMapping
    public String addRecipe(@Valid AddRecipeRequest addRecipeRequest,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model) {

        model.addAttribute("pageTitle", "Add Recipe");

        if (bindingResult.hasErrors()) {
            return "add-recipe";
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        recipeService.addRecipe(addRecipeRequest, user);

        return "redirect:/recipes/all-recipes";
    }

    @GetMapping("/edit-recipe")
    public String getEditRecipePage(Model model) {
        model.addAttribute("pageTitle", "Edit Recipe");
        return "edit-recipe";
    }
}
