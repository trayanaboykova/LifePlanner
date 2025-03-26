package lifeplanner.recipes;

import lifeplanner.exception.DomainException;
import lifeplanner.exception.recipes.*;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeCategory;
import lifeplanner.recipes.model.RecipeDifficulty;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.recipes.service.RecipeLikesService;
import lifeplanner.recipes.service.RecipeService;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddRecipeRequest;
import lifeplanner.web.dto.EditRecipeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceUTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeLikesService recipeLikesService;

    @Mock
    private RecipeFavoriteService recipeFavoriteService;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void givenNullUser_whenGetRecipeByUser_thenThrowDomainException() {
        assertThrows(DomainException.class, () -> recipeService.getRecipeByUser(null));
        verifyNoInteractions(recipeRepository);
    }

    @Test
    void givenValidUser_whenGetRecipeByUser_thenReturnRecipes() {
        User user = new User();
        List<Recipe> recipes = List.of(new Recipe(), new Recipe());
        when(recipeRepository.findAllByOwner(user)).thenReturn(recipes);

        List<Recipe> result = recipeService.getRecipeByUser(user);

        assertEquals(2, result.size());
        verify(recipeRepository).findAllByOwner(user);
    }

    @Test
    void givenAddRecipeRequest_whenAddRecipe_thenSaveRecipeWithIngredients() {
        User user = new User();
        AddRecipeRequest request = new AddRecipeRequest();
        request.setName("Test Recipe");
        request.setCategory(RecipeCategory.DESSERT);
        request.setDifficulty(RecipeDifficulty.MEDIUM);
        request.setCuisine("Italian");
        request.setCookingTime(30);
        request.setInstructions("Test instructions");
        request.setIngredient(List.of("Flour", "Sugar"));
        request.setQuantity(List.of(200.0, 100.0));
        request.setUnit(List.of("g", "g"));

        recipeService.addRecipe(request, user);

        verify(recipeRepository).save(argThat(recipe -> {
            assertEquals("Test Recipe", recipe.getName());
            assertEquals(2, recipe.getIngredients().size());
            assertFalse(recipe.isVisible());
            assertEquals(ApprovalStatus.PENDING, recipe.getApprovalStatus());
            return true;
        }));
    }

    @Test
    void givenNonExistingRecipeId_whenGetRecipeById_thenThrowRecipeNotFoundException() {
        UUID recipeId = UUID.randomUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById(recipeId));
    }

    @Test
    void givenExistingRecipe_whenEditRecipe_thenUpdateRecipe() {
        UUID recipeId = UUID.randomUUID();
        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setName("Old Name");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));

        EditRecipeRequest editRequest = EditRecipeRequest.builder().build();
        editRequest.setName("New Name");
        editRequest.setCategory(RecipeCategory.DESSERT);
        editRequest.setIngredient(List.of("Chicken"));
        editRequest.setQuantity(List.of(500.0));
        editRequest.setUnit(List.of("g"));

        recipeService.editRecipe(recipeId, editRequest);

        verify(recipeRepository).save(argThat(recipe -> {
            assertEquals("New Name", recipe.getName());
            assertEquals(1, recipe.getIngredients().size());
            return true;
        }));
    }

    @Test
    void givenNonExistingRecipeId_whenShareRecipe_thenThrowRecipeNotFoundException() {
        UUID recipeId = UUID.randomUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.shareRecipe(recipeId));
    }

    @Test
    void givenAlreadyVisibleRecipe_whenShareRecipe_thenThrowRecipeAlreadySharedException() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setVisible(true);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(RecipeAlreadySharedException.class, () -> recipeService.shareRecipe(recipeId));
    }

    @Test
    void givenRejectedRecipe_whenShareRecipe_thenThrowRecipeRejectedException() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.REJECTED);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(RecipeRejectedException.class, () -> recipeService.shareRecipe(recipeId));
    }

    @Test
    void givenPendingRecipe_whenShareRecipe_thenThrowRecipePendingApprovalException() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.PENDING);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(RecipePendingApprovalException.class, () -> recipeService.shareRecipe(recipeId));
    }

    @Test
    void givenApprovedRecipe_whenShareRecipe_thenSetVisibleTrue() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.APPROVED);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.shareRecipe(recipeId);

        assertTrue(recipe.isVisible());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void whenGetAllRecipes_thenReturnAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(List.of(new Recipe(), new Recipe()));

        List<Recipe> recipes = recipeService.getAllRecipes();

        assertEquals(2, recipes.size());
    }

    @Test
    void givenRecipes_whenGetLikeCountsForRecipes_thenReturnCounts() {
        Recipe recipe1 = new Recipe();
        recipe1.setId(UUID.randomUUID());
        Recipe recipe2 = new Recipe();
        recipe2.setId(UUID.randomUUID());

        when(recipeLikesService.getLikeCount(recipe1.getId())).thenReturn(5L);
        when(recipeLikesService.getLikeCount(recipe2.getId())).thenReturn(3L);

        Map<UUID, Long> counts = recipeService.getLikeCountsForRecipes(List.of(recipe1, recipe2));

        assertEquals(5L, counts.get(recipe1.getId()));
        assertEquals(3L, counts.get(recipe2.getId()));
    }

    @Test
    void givenCurrentUser_whenGetApprovedSharedRecipes_thenReturnOthersRecipes() {
        // Create users with IDs
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());  // Set ID for current user

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());    // Set ID for other user

        Recipe recipe1 = new Recipe();
        recipe1.setOwner(otherUser);
        recipe1.setApprovalStatus(ApprovalStatus.APPROVED);
        recipe1.setVisible(true);

        Recipe recipe2 = new Recipe();
        recipe2.setOwner(currentUser);
        recipe2.setApprovalStatus(ApprovalStatus.APPROVED);
        recipe2.setVisible(true);

        when(recipeRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED))
                .thenReturn(List.of(recipe1, recipe2));

        List<Recipe> result = recipeService.getApprovedSharedRecipes(currentUser);

        assertEquals(1, result.size());
        assertTrue(result.contains(recipe1));
    }

    @Test
    void givenCurrentUser_whenGetMySharedRecipes_thenReturnUsersRecipes() {
        // Create users with IDs
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());  // Set ID for current user

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());    // Set ID for other user

        Recipe recipe1 = new Recipe();
        recipe1.setOwner(currentUser);
        recipe1.setVisible(true);

        Recipe recipe2 = new Recipe();
        recipe2.setOwner(otherUser);
        recipe2.setVisible(true);

        when(recipeRepository.findAllByVisibleTrue()).thenReturn(List.of(recipe1, recipe2));

        List<Recipe> result = recipeService.getMySharedRecipes(currentUser);

        assertEquals(1, result.size());
        assertTrue(result.contains(recipe1));
    }

    @Test
    void givenNonExistingRecipeId_whenRemoveSharing_thenThrowRecipeNotFoundException() {
        UUID recipeId = UUID.randomUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.removeSharing(recipeId));
        verify(recipeRepository, never()).save(any());
    }

    @Test
    void givenVisibleRecipe_whenRemoveSharing_thenSetVisibleFalse() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setVisible(true);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.removeSharing(recipeId);

        assertFalse(recipe.isVisible());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void whenDeleteRecipeById_thenCallRepositoryDelete() {
        UUID recipeId = UUID.randomUUID();
        recipeService.deleteRecipeById(recipeId);
        verify(recipeRepository).deleteById(recipeId);
    }

    @Test
    void whenGetPendingRecipes_thenReturnPendingRecipes() {
        Recipe recipe1 = new Recipe();
        recipe1.setApprovalStatus(ApprovalStatus.PENDING);
        Recipe recipe2 = new Recipe();
        recipe2.setApprovalStatus(ApprovalStatus.PENDING);

        when(recipeRepository.findAllByApprovalStatus(ApprovalStatus.PENDING))
                .thenReturn(List.of(recipe1, recipe2));

        List<Recipe> result = recipeService.getPendingRecipes();

        assertEquals(2, result.size());
    }

    @Test
    void givenNonExistingRecipeId_whenApproveRecipe_thenThrowRecipeNotFoundException() {
        UUID recipeId = UUID.randomUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.approveRecipe(recipeId));
    }

    @Test
    void givenAlreadyApprovedRecipe_whenApproveRecipe_thenThrowRecipeAlreadyApprovedException() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.APPROVED);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(RecipeAlreadyApprovedException.class, () -> recipeService.approveRecipe(recipeId));
    }

    @Test
    void givenPendingRecipe_whenApproveRecipe_thenSetApproved() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.PENDING);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.approveRecipe(recipeId);

        assertEquals(ApprovalStatus.APPROVED, recipe.getApprovalStatus());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void givenNonExistingRecipeId_whenRejectRecipe_thenThrowRecipeNotFoundException() {
        UUID recipeId = UUID.randomUUID();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.rejectRecipe(recipeId));
    }

    @Test
    void givenAlreadyRejectedRecipe_whenRejectRecipe_thenThrowRecipeAlreadyRejectedException() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.REJECTED);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(RecipeAlreadyRejectedException.class, () -> recipeService.rejectRecipe(recipeId));
    }

    @Test
    void givenPendingRecipe_whenRejectRecipe_thenSetRejected() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApprovalStatus(ApprovalStatus.PENDING);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.rejectRecipe(recipeId);

        assertEquals(ApprovalStatus.REJECTED, recipe.getApprovalStatus());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void testGetFavoriteCountsForRecipes() {
        // Create two recipes with valid IDs.
        Recipe recipe1 = new Recipe();
        UUID id1 = UUID.randomUUID();
        recipe1.setId(id1);

        Recipe recipe2 = new Recipe();
        UUID id2 = UUID.randomUUID();
        recipe2.setId(id2);

        // Stub the favorite count for each recipe.
        when(recipeFavoriteService.getFavoriteCount(id1)).thenReturn(10L);
        when(recipeFavoriteService.getFavoriteCount(id2)).thenReturn(5L);

        // Call the method under test.
        Map<UUID, Long> result = recipeService.getFavoriteCountsForRecipes(List.of(recipe1, recipe2));

        // Verify that the map contains the expected counts.
        assertEquals(10L, result.get(id1));
        assertEquals(5L, result.get(id2));
        assertEquals(2, result.size());
    }

    @Test
    void testGetApprovedSharedRecipesFiltersInvalidOwners() {
        // Setup current user.
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        // Recipe with a null owner (should be filtered out)
        Recipe recipeNullOwner = new Recipe();
        recipeNullOwner.setId(UUID.randomUUID());
        recipeNullOwner.setApprovalStatus(ApprovalStatus.APPROVED);
        recipeNullOwner.setVisible(true);
        // owner is left as null

        // Recipe with owner that has null id (should be filtered out)
        Recipe recipeOwnerNullId = new Recipe();
        recipeOwnerNullId.setId(UUID.randomUUID());
        recipeOwnerNullId.setApprovalStatus(ApprovalStatus.APPROVED);
        recipeOwnerNullId.setVisible(true);
        User ownerNullId = new User();
        // ownerNullId id remains null
        recipeOwnerNullId.setOwner(ownerNullId);

        // Recipe with owner equal to currentUser (should be filtered out)
        Recipe recipeSameOwner = new Recipe();
        recipeSameOwner.setId(UUID.randomUUID());
        recipeSameOwner.setApprovalStatus(ApprovalStatus.APPROVED);
        recipeSameOwner.setVisible(true);
        recipeSameOwner.setOwner(currentUser);

        // Recipe with a valid owner different from currentUser (should be included)
        Recipe recipeDifferentOwner = new Recipe();
        recipeDifferentOwner.setId(UUID.randomUUID());
        recipeDifferentOwner.setApprovalStatus(ApprovalStatus.APPROVED);
        recipeDifferentOwner.setVisible(true);
        User differentOwner = new User();
        differentOwner.setId(UUID.randomUUID());
        recipeDifferentOwner.setOwner(differentOwner);

        // Stub the repository call.
        when(recipeRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED))
                .thenReturn(List.of(recipeNullOwner, recipeOwnerNullId, recipeSameOwner, recipeDifferentOwner));

        // Call the method under test.
        List<Recipe> result = recipeService.getApprovedSharedRecipes(currentUser);

        // Expect only recipeDifferentOwner to be included.
        assertEquals(1, result.size());
        assertTrue(result.contains(recipeDifferentOwner));
    }

    @Test
    void testGetMySharedRecipesFiltersInvalidOwners() {
        // Setup current user.
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        // Recipe with a null owner (should be filtered out)
        Recipe recipeNullOwner = new Recipe();
        recipeNullOwner.setId(UUID.randomUUID());
        recipeNullOwner.setVisible(true);
        // owner remains null

        // Recipe with owner that has null id (should be filtered out)
        Recipe recipeOwnerNullId = new Recipe();
        recipeOwnerNullId.setId(UUID.randomUUID());
        recipeOwnerNullId.setVisible(true);
        User ownerNullId = new User(); // id not set
        recipeOwnerNullId.setOwner(ownerNullId);

        // Recipe with owner equal to currentUser (should be included)
        Recipe recipeSameOwner = new Recipe();
        recipeSameOwner.setId(UUID.randomUUID());
        recipeSameOwner.setVisible(true);
        recipeSameOwner.setOwner(currentUser);

        // Recipe with a valid owner different from currentUser (should be filtered out)
        Recipe recipeDifferentOwner = new Recipe();
        recipeDifferentOwner.setId(UUID.randomUUID());
        recipeDifferentOwner.setVisible(true);
        User differentOwner = new User();
        differentOwner.setId(UUID.randomUUID());
        recipeDifferentOwner.setOwner(differentOwner);

        // Stub the repository call.
        when(recipeRepository.findAllByVisibleTrue())
                .thenReturn(List.of(recipeNullOwner, recipeOwnerNullId, recipeSameOwner, recipeDifferentOwner));

        // Call the method under test.
        List<Recipe> result = recipeService.getMySharedRecipes(currentUser);

        // Expect only recipeSameOwner to be included.
        assertEquals(1, result.size());
        assertTrue(result.contains(recipeSameOwner));
    }

}