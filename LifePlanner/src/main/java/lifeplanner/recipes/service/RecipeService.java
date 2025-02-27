package lifeplanner.recipes.service;

import jakarta.validation.Valid;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeIngredient;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddRecipeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getRecipeByUser(User user) {
        return recipeRepository.findAllByOwner(user);
    }

    public void addRecipe(@Valid AddRecipeRequest addRecipeRequest, User user) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        List<String> ingredientNames = addRecipeRequest.getIngredient();
        List<Double> quantities = addRecipeRequest.getQuantity();
        List<String> units = addRecipeRequest.getUnit();

        if (ingredientNames != null) {
            for (int i = 0; i < ingredientNames.size(); i++) {
                RecipeIngredient ingredient = RecipeIngredient.builder()
                        .ingredientName(ingredientNames.get(i))
                        .quantity(quantities != null && quantities.size() > i ? quantities.get(i) : null)
                        .unit(units != null && units.size() > i ? units.get(i) : null)
                        .build();
                ingredients.add(ingredient);
            }
        }
        Recipe recipe = Recipe.builder()
                .name(addRecipeRequest.getName())
                .category(addRecipeRequest.getCategory())
                .difficulty(addRecipeRequest.getDifficulty())
                .cuisine(addRecipeRequest.getCuisine())
                .cookingTime(addRecipeRequest.getCookingTime())
                .instructions(addRecipeRequest.getInstructions())
                .owner(user)
                .ingredients(ingredients)
                .build();

        for (RecipeIngredient ingredient : ingredients) {
            ingredient.setRecipe(recipe);
        }
        recipeRepository.save(recipe);
    }
}
