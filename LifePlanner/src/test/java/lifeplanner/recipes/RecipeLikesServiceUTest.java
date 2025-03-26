package lifeplanner.recipes;

import lifeplanner.exception.DomainException;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeLikes;
import lifeplanner.recipes.model.RecipeLikesId;
import lifeplanner.recipes.repository.RecipeLikesRepository;
import lifeplanner.recipes.repository.RecipeRepository;
import lifeplanner.recipes.service.RecipeLikesService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeLikesServiceUTest {

    @Mock
    private RecipeLikesRepository recipeLikesRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeLikesService recipeLikesService;

    @Test
    void givenRecipeId_whenGetLikeCount_thenReturnCount() {
        UUID recipeId = UUID.randomUUID();
        when(recipeLikesRepository.countByRecipeId(recipeId)).thenReturn(5L);

        long count = recipeLikesService.getLikeCount(recipeId);

        assertEquals(5L, count);
    }

    @Test
    void givenExistingLike_whenToggleLike_thenRemoveLike() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RecipeLikesId likeId = new RecipeLikesId(recipeId, userId);
        when(recipeLikesRepository.existsById(likeId)).thenReturn(true);

        boolean result = recipeLikesService.toggleLike(recipeId, userId);

        assertFalse(result);
        verify(recipeLikesRepository).deleteById(likeId);
    }

    @Test
    void givenNoExistingLike_whenToggleLike_thenAddLike() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        User user = new User();

        when(recipeLikesRepository.existsById(any())).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = recipeLikesService.toggleLike(recipeId, userId);

        assertTrue(result);
        verify(recipeLikesRepository).save(any(RecipeLikes.class));
    }

    @Test
    void givenNonExistingRecipe_whenToggleLike_thenThrowDomainException() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(recipeLikesRepository.existsById(any())).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class,
                () -> recipeLikesService.toggleLike(recipeId, userId));
        assertEquals("Recipe not found", exception.getMessage());
    }

    @Test
    void givenNonExistingUser_whenToggleLike_thenThrowDomainException() {
        UUID recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(recipeLikesRepository.existsById(any())).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(new Recipe()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class,
                () -> recipeLikesService.toggleLike(recipeId, userId));
        assertEquals("User not found", exception.getMessage());
    }
}