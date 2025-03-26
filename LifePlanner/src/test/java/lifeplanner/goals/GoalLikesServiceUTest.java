package lifeplanner.goals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.DomainException;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalLikes;
import lifeplanner.goals.model.GoalLikesId;
import lifeplanner.goals.repository.GoalLikesRepository;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.goals.service.GoalLikesService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GoalLikesServiceUTest {

    @Mock
    private GoalLikesRepository goalLikesRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalLikesService goalLikesService;

    @Test
    void testGetLikeCount() {
        UUID goalId = UUID.randomUUID();
        when(goalLikesRepository.countByGoalId(goalId)).thenReturn(12L);
        long count = goalLikesService.getLikeCount(goalId);
        assertEquals(12L, count);
    }

    @Test
    void testToggleLikeRemove() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalLikesId likeId = new GoalLikesId(goalId, userId);
        when(goalLikesRepository.existsById(likeId)).thenReturn(true);

        boolean result = goalLikesService.toggleLike(goalId, userId);
        assertFalse(result);
        verify(goalLikesRepository).deleteById(likeId);
    }

    @Test
    void testToggleLikeAdd() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalLikesId likeId = new GoalLikesId(goalId, userId);
        when(goalLikesRepository.existsById(likeId)).thenReturn(false);

        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = goalLikesService.toggleLike(goalId, userId);
        assertTrue(result);
        ArgumentCaptor<GoalLikes> captor = ArgumentCaptor.forClass(GoalLikes.class);
        verify(goalLikesRepository).save(captor.capture());
        GoalLikes savedLike = captor.getValue();
        assertEquals(likeId, savedLike.getId());
        assertEquals(goal, savedLike.getGoal());
        assertEquals(user, savedLike.getUser());
    }

    @Test
    void testToggleLikeGoalNotFound() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalLikesId likeId = new GoalLikesId(goalId, userId);
        when(goalLikesRepository.existsById(likeId)).thenReturn(false);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () ->
                goalLikesService.toggleLike(goalId, userId));
        assertEquals("Goal not found", ex.getMessage());
    }

    @Test
    void testToggleLikeUserNotFound() {
        UUID goalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GoalLikesId likeId = new GoalLikesId(goalId, userId);
        when(goalLikesRepository.existsById(likeId)).thenReturn(false);
        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () ->
                goalLikesService.toggleLike(goalId, userId));
        assertEquals("User not found", ex.getMessage());
    }
}