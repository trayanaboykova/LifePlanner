package lifeplanner.recipes.service;

import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeIngredient;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddRecipeRequest;
import lifeplanner.web.dto.EditRecipeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                .visible(false)
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        for (RecipeIngredient ingredient : ingredients) {
            ingredient.setRecipe(recipe);
        }
        recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(UUID recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe with id [" + recipeId + "] does not exist."));
    }

    public void editRecipe(UUID recipeId, EditRecipeRequest editRecipeRequest) {
        Recipe recipe = getRecipeById(recipeId);

        recipe.setName(editRecipeRequest.getName());
        recipe.setCategory(editRecipeRequest.getCategory());
        recipe.setDifficulty(editRecipeRequest.getDifficulty());
        recipe.setCuisine(editRecipeRequest.getCuisine());
        recipe.setCookingTime(editRecipeRequest.getCookingTime());
        recipe.setInstructions(editRecipeRequest.getInstructions());

        // Create a new list of RecipeIngredient objects from the DTO data.
        List<RecipeIngredient> ingredients = new ArrayList<>();
        List<String> ingredientNames = editRecipeRequest.getIngredient();
        List<Double> quantities = editRecipeRequest.getQuantity();
        List<String> units = editRecipeRequest.getUnit();

        if (ingredientNames != null) {
            for (int i = 0; i < ingredientNames.size(); i++) {
                RecipeIngredient ingredient = RecipeIngredient.builder()
                        .ingredientName(ingredientNames.get(i))
                        .quantity(quantities != null && quantities.size() > i ? quantities.get(i) : null)
                        .unit(units != null && units.size() > i ? units.get(i) : null)
                        .recipe(recipe) // Set the back-reference to the current recipe
                        .build();
                ingredients.add(ingredient);
            }
        }
        // Replace the existing ingredients list with the new one.
        recipe.setIngredients(ingredients);

        recipeRepository.save(recipe);
    }

    public void shareRecipe(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.setVisible(true);
        recipeRepository.save(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public List<Recipe> getSharedRecipes(User currentUser) {
        return recipeRepository.findAllByVisibleTrue()
                .stream()
                .filter(recipe -> !recipe.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Recipe> getMySharedRecipes(User currentUser) {
        return recipeRepository.findAllByVisibleTrue()
                .stream()
                .filter(recipe -> recipe.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public void removeSharing(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        recipe.setVisible(false);
        recipeRepository.save(recipe);
    }

    public void deleteRecipeById(UUID id) {
        recipeRepository.deleteById(id);
    }

    public List<Recipe> getPendingRecipes() {
        return recipeRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    public void approveRecipe(UUID recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.APPROVED);
        recipeRepository.save(recipe);
    }

    public void rejectRecipe(UUID recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.REJECTED);
        recipeRepository.save(recipe);
    }
}