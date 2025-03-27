package lifeplanner.web.recipes;

import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import lifeplanner.web.dto.AddRecipeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipesController.class)
public class RecipesControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final UUID recipeId = UUID.randomUUID();
    private final User testUser = new User();
    private final Recipe testRecipe = new Recipe();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(userId, "user", "password", UserRole.USER, true);
    }

    @Test
    void getAllRecipesPage_ShouldReturnAllRecipesView() throws Exception {
        testUser.setId(userId);
        testRecipe.setId(recipeId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(recipeService.getRecipeByUser(testUser)).thenReturn(Arrays.asList(testRecipe));

        mockMvc.perform(get("/recipes/all-recipes").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-recipes"))
                .andExpect(model().attribute("pageTitle", "All Recipes"))
                .andExpect(model().attribute("recipes", Arrays.asList(testRecipe)));
    }

    @Test
    void showAddRecipeRequest_ShouldReturnAddRecipeView() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/recipes/new").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-recipe"))
                .andExpect(model().attributeExists("pageTitle", "user", "addRecipeRequest"));
    }

    @Test
    void addRecipe_WithValidRequest_ShouldRedirect() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/recipes")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("name", "Test Recipe")
                        .param("ingredients", "Ingredient1, Ingredient2")
                        .param("instructions", "Step 1, Step 2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes/all-recipes"));

        verify(recipeService).addRecipe(any(AddRecipeRequest.class), eq(testUser));
    }

    @Test
    void addRecipe_WithInvalidRequest_ShouldReturnAddRecipeForm() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(post("/recipes")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("ingredients", "Ingredient1, Ingredient2")
                        .param("instructions", "Step 1, Step 2"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-recipe"));
    }

    @Test
    void showEditRecipeRequest_ShouldReturnEditRecipeView() throws Exception {
        testRecipe.setId(recipeId);
        when(recipeService.getRecipeById(recipeId)).thenReturn(testRecipe);

        mockMvc.perform(get("/recipes/{id}/edit", recipeId).with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-recipe"))
                .andExpect(model().attributeExists("pageTitle", "recipe", "editRecipeRequest"));
    }

    @Test
    void updateRecipe_WithValidRequest_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/recipes/{id}/edit", recipeId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("name", "Updated Recipe Title")
                        .param("ingredients", "Updated Ingredients")
                        .param("instructions", "Updated Instructions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes/all-recipes"));
    }

    @Test
    void updateRecipe_WithBindingErrors_ShouldReturnEditRecipeView() throws Exception {
        testRecipe.setId(recipeId);
        when(recipeService.getRecipeById(recipeId)).thenReturn(testRecipe);

        mockMvc.perform(post("/recipes/{id}/edit", recipeId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("name", "")
                        .param("ingredients", "Some Ingredients")
                        .param("instructions", "Some Instructions"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-recipe"))
                .andExpect(model().attributeExists("recipe", "editRecipeRequest"));
    }

    @Test
    void shareRecipe_ShouldRedirectToAllRecipes() throws Exception {
        mockMvc.perform(post("/recipes/{id}/share", recipeId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes/all-recipes"));
    }

    @Test
    void removeSharing_ShouldRedirectToMySharedPosts() throws Exception {
        mockMvc.perform(post("/recipes/{id}/remove", recipeId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-shared-posts"));
    }

    @Test
    void deleteRecipe_ShouldRedirectToAllRecipes() throws Exception {
        mockMvc.perform(delete("/recipes/{id}", recipeId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes/all-recipes"));
    }
}