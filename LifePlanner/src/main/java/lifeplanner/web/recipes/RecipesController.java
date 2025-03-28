package lifeplanner.web.recipes;

import jakarta.validation.Valid;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddRecipeRequest;
import lifeplanner.web.dto.EditRecipeRequest;
import lifeplanner.web.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public String getAllRecipesPage(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "All Recipes");

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Recipe> userRecipe = recipeService.getRecipeByUser(user);

        model.addAttribute("recipes", userRecipe);

        return "all-recipes";
    }

    @GetMapping("/new")
    public ModelAndView showAddRecipeRequest(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        model.addAttribute("pageTitle", "Add Recipe");

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-recipe");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addRecipeRequest", new AddRecipeRequest());

        return modelAndView;
    }

    @PostMapping
    public String addRecipe(@Valid AddRecipeRequest addRecipeRequest,
                            BindingResult bindingResult,
                            Model model,
                            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        model.addAttribute("pageTitle", "Add Recipe");

        if (bindingResult.hasErrors()) {
            return "add-recipe";
        }

        User user = userService.getById(authenticationMetadata.getUserId());

        recipeService.addRecipe(addRecipeRequest, user);

        return "redirect:/recipes/all-recipes";
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditRecipeRequest(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("pageTitle", "Edit Recipe");

        Recipe recipe = recipeService.getRecipeById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-recipe");
        modelAndView.addObject("recipe", recipe);
        modelAndView.addObject("editRecipeRequest", DTOMapper.mapRecipeToEditRecipeRequest(recipe));
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateBook(@PathVariable("id") UUID id,
                                   @Valid @ModelAttribute("editRecipeRequest") EditRecipeRequest editRecipeRequest,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Recipe recipe = recipeService.getRecipeById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-recipe");
            modelAndView.addObject("recipe", recipe);
            modelAndView.addObject("editRecipeRequest", editRecipeRequest);
            return modelAndView;
        }

        recipeService.editRecipe(id, editRecipeRequest);
        return new ModelAndView("redirect:/recipes/all-recipes");
    }

    @PostMapping("/{id}/share")
    public String shareRecipe(@PathVariable UUID id) {

        recipeService.shareRecipe(id);

        return "redirect:/recipes/all-recipes";
    }

    @PostMapping("/{id}/remove")
    public String removeSharing(@PathVariable UUID id) {
        recipeService.removeSharing(id);
        return "redirect:/my-shared-posts";
    }

    @DeleteMapping("/{id}")
    public String deleteRecipe(@PathVariable UUID id) {

        recipeService.deleteRecipeById(id);

        return "redirect:/recipes/all-recipes";
    }
}