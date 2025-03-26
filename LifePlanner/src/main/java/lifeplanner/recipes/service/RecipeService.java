package lifeplanner.recipes.service;

import jakarta.validation.Valid;
import lifeplanner.exception.DomainException;
import lifeplanner.exception.recipes.*;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeIngredient;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddRecipeRequest;
import lifeplanner.web.dto.EditRecipeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeLikesService recipeLikesService;
    private final RecipeFavoriteService recipeFavoriteService;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, RecipeLikesService recipeLikesService, RecipeFavoriteService recipeFavoriteService) {
        this.recipeRepository = recipeRepository;
        this.recipeLikesService = recipeLikesService;
        this.recipeFavoriteService = recipeFavoriteService;
    }

    public List<Recipe> getRecipeByUser(User user) {
        if (user == null) {
            throw new DomainException("User must be provided");
        }
        return recipeRepository.findAllByOwner(user);
    }

    @Transactional
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
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
    }

    @Transactional
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

    @Transactional
    public void shareRecipe(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (recipe.isVisible()) {
            throw new RecipeAlreadySharedException(recipeId);
        }

        if (recipe.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new RecipeRejectedException(recipeId);
        }

        if (recipe.getApprovalStatus() == ApprovalStatus.PENDING) {
            throw new RecipePendingApprovalException(recipeId);
        }

        recipe.setVisible(true);
        recipeRepository.save(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Map<UUID, Long> getLikeCountsForRecipes(List<Recipe> recipes) {
        return recipes.stream()
                .collect(Collectors.toMap(
                        Recipe::getId,
                        recipe -> recipeLikesService.getLikeCount(recipe.getId())
                ));
    }

    public Map<UUID, Long> getFavoriteCountsForRecipes(List<Recipe> recipes) {
        return recipes.stream()
                .collect(Collectors.toMap(
                        Recipe::getId,
                        recipe -> recipeFavoriteService.getFavoriteCount(recipe.getId())
                ));
    }

    public List<Recipe> getApprovedSharedRecipes(User currentUser) {
        List<Recipe> approvedRecipes = recipeRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED);
        return approvedRecipes.stream()
                .filter(recipe -> recipe.getOwner() != null
                        && recipe.getOwner().getId() != null
                        && !recipe.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Recipe> getMySharedRecipes(User currentUser) {
        return recipeRepository.findAllByVisibleTrue()
                .stream()
                .filter(recipe -> recipe.getOwner() != null
                        && recipe.getOwner().getId() != null
                        && recipe.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    @Transactional
    public void removeSharing(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
        recipe.setVisible(false);
        recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteRecipeById(UUID id) {
        recipeRepository.deleteById(id);
    }

    public List<Recipe> getPendingRecipes() {
        return recipeRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Transactional
    public void approveRecipe(UUID recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        if (recipe.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new RecipeAlreadyApprovedException(recipeId);
        }
        recipe.setApprovalStatus(ApprovalStatus.APPROVED);
        recipeRepository.save(recipe);
    }

    @Transactional
    public void rejectRecipe(UUID recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        if (recipe.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new RecipeAlreadyRejectedException(recipeId);
        }
        recipe.setApprovalStatus(ApprovalStatus.REJECTED);
        recipeRepository.save(recipe);
    }
}