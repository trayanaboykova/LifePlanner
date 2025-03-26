package lifeplanner.recipes;

import lifeplanner.exception.DomainException;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeFavorite;
import lifeplanner.recipes.model.RecipeFavoriteId;
import lifeplanner.recipes.repository.RecipeFavoriteRepository;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.recipes.service.RecipeFavoriteService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeFavoriteServiceUTest {

    @Mock
    private RecipeFavoriteRepository recipeFavoriteRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeFavoriteService recipeFavoriteService;

    @Test
    void givenRecipeId_whenGetFavoriteCount_thenReturnCount() {
        UUID recipeId = UUID.randomUUID();
        when(recipeFavoriteRepository.countByRecipeId(recipeId)).thenReturn(3L);

        long count = recipeFavoriteService.getFavoriteCount(recipeId);

        assertEquals(3L, count);
    }

    @Test
    void givenExistingFavorite_whenToggleFavorite_thenRemoveFavorite() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RecipeFavoriteId favoriteId = new RecipeFavoriteId(recipeId, userId);
        when(recipeFavoriteRepository.existsById(favoriteId)).thenReturn(true);

        boolean result = recipeFavoriteService.toggleFavorite(recipeId, userId);

        assertFalse(result);
        verify(recipeFavoriteRepository).deleteById(favoriteId);
    }

    @Test
    void givenNoExistingFavorite_whenToggleFavorite_thenAddFavorite() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        User user = new User();

        when(recipeFavoriteRepository.existsById(any())).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = recipeFavoriteService.toggleFavorite(recipeId, userId);

        assertTrue(result);
        verify(recipeFavoriteRepository).save(any(RecipeFavorite.class));
    }

    @Test
    void givenNonExistingRecipe_whenToggleFavorite_thenThrowDomainException() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(recipeFavoriteRepository.existsById(any())).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class,
                () -> recipeFavoriteService.toggleFavorite(recipeId, userId));
        assertEquals("Recipe not found", exception.getMessage());
    }

    @Test
    void givenNonExistingUser_whenToggleFavorite_thenThrowDomainException() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(recipeFavoriteRepository.existsById(any())).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(new Recipe()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class,
                () -> recipeFavoriteService.toggleFavorite(recipeId, userId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void givenUser_whenGetFavoritesByUser_thenReturnFavorites() {
        User user = new User();
        Recipe recipe = new Recipe();
        RecipeFavorite favorite = new RecipeFavorite();
        favorite.setRecipe(recipe);

        when(recipeFavoriteRepository.findAllByUser(user)).thenReturn(List.of(favorite));

        List<Recipe> result = recipeFavoriteService.getFavoritesByUser(user);

        assertEquals(1, result.size());
        assertTrue(result.contains(recipe));
    }

    @Test
    void givenUserWithNoFavorites_whenGetFavoritesByUser_thenReturnEmptyList() {
        User user = new User();
        when(recipeFavoriteRepository.findAllByUser(user)).thenReturn(Collections.emptyList());

        List<Recipe> result = recipeFavoriteService.getFavoritesByUser(user);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenExistingFavorite_whenRemoveFavorite_thenDeleteFavorite() {
        User user = new User();
        UUID recipeId = UUID.randomUUID();
        RecipeFavorite favorite = new RecipeFavorite();
        when(recipeFavoriteRepository.findByUserAndRecipeId(user, recipeId))
                .thenReturn(Optional.of(favorite));

        recipeFavoriteService.removeFavorite(user, recipeId);

        verify(recipeFavoriteRepository).delete(favorite);
    }

    @Test
    void givenNonExistingFavorite_whenRemoveFavorite_thenDoNothing() {
        User user = new User();
        UUID recipeId = UUID.randomUUID();
        when(recipeFavoriteRepository.findByUserAndRecipeId(user, recipeId))
                .thenReturn(Optional.empty());

        recipeFavoriteService.removeFavorite(user, recipeId);

        verify(recipeFavoriteRepository, never()).delete(any());
    }
}