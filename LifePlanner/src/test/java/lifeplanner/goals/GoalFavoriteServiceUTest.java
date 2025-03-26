package lifeplanner.goals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.DomainException;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalFavorite;
import lifeplanner.goals.model.GoalFavoriteId;
import lifeplanner.goals.repository.GoalFavoriteRepository;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class GoalFavoriteServiceUTest {

    @Mock
    private GoalFavoriteRepository goalFavoriteRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalFavoriteService goalFavoriteService;

    @Test
    void testGetFavoriteCount() {
        UUID goalId = UUID.randomUUID();
        when(goalFavoriteRepository.countByGoalId(goalId)).thenReturn(6L);
        long count = goalFavoriteService.getFavoriteCount(goalId);
        assertEquals(6L, count);
    }

    @Test
    void testToggleFavoriteRemove() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalFavoriteId favoriteId = new GoalFavoriteId(goalId, userId);
        when(goalFavoriteRepository.existsById(favoriteId)).thenReturn(true);

        boolean result = goalFavoriteService.toggleFavorite(goalId, userId);
        assertFalse(result);
        verify(goalFavoriteRepository).deleteById(favoriteId);
    }

    @Test
    void testToggleFavoriteAdd() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalFavoriteId favoriteId = new GoalFavoriteId(goalId, userId);
        when(goalFavoriteRepository.existsById(favoriteId)).thenReturn(false);

        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = goalFavoriteService.toggleFavorite(goalId, userId);
        assertTrue(result);
        ArgumentCaptor<GoalFavorite> captor = ArgumentCaptor.forClass(GoalFavorite.class);
        verify(goalFavoriteRepository).save(captor.capture());
        GoalFavorite savedFavorite = captor.getValue();
        assertEquals(favoriteId, savedFavorite.getId());
        assertEquals(goal, savedFavorite.getGoal());
        assertEquals(user, savedFavorite.getUser());
    }

    @Test
    void testToggleFavoriteGoalNotFound() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalFavoriteId favoriteId = new GoalFavoriteId(goalId, userId);
        when(goalFavoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> goalFavoriteService.toggleFavorite(goalId, userId));
        assertEquals("Goal not found", ex.getMessage());
    }

    @Test
    void testToggleFavoriteUserNotFound() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalFavoriteId favoriteId = new GoalFavoriteId(goalId, userId);
        when(goalFavoriteRepository.existsById(favoriteId)).thenReturn(false);
        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> goalFavoriteService.toggleFavorite(goalId, userId));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testGetFavoritesByUser() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Goal goal1 = new Goal();
        goal1.setId(UUID.randomUUID());
        Goal goal2 = new Goal();
        goal2.setId(UUID.randomUUID());

        GoalFavorite fav1 = new GoalFavorite();
        fav1.setGoal(goal1);
        GoalFavorite fav2 = new GoalFavorite();
        fav2.setGoal(goal2);

        when(goalFavoriteRepository.findAllByUser(user)).thenReturn(List.of(fav1, fav2));

        List<Goal> favorites = goalFavoriteService.getFavoritesByUser(user);
        assertEquals(2, favorites.size());
        assertTrue(favorites.contains(goal1));
        assertTrue(favorites.contains(goal2));
    }

    @Test
    void testRemoveFavoritePresent() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID goalId = UUID.randomUUID();

        GoalFavorite favorite = new GoalFavorite();
        when(goalFavoriteRepository.findByUserAndGoalId(user, goalId))
                .thenReturn(Optional.of(favorite));

        goalFavoriteService.removeFavorite(user, goalId);
        verify(goalFavoriteRepository).delete(favorite);
    }

    @Test
    void testRemoveFavoriteNotPresent() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID goalId = UUID.randomUUID();

        when(goalFavoriteRepository.findByUserAndGoalId(user, goalId))
                .thenReturn(Optional.empty());

        // Should do nothing if favorite is not found.
        goalFavoriteService.removeFavorite(user, goalId);
        verify(goalFavoriteRepository, never()).delete(any());
    }
}