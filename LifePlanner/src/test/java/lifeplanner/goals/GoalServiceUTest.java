package lifeplanner.goals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.goals.*;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalCategory;
import lifeplanner.goals.model.GoalPriority;
import lifeplanner.goals.model.GoalStatus;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.goals.service.GoalFavoriteService;
import lifeplanner.goals.service.GoalLikesService;
import lifeplanner.goals.service.GoalService;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.util.ProgressUtil;
import lifeplanner.web.dto.AddGoalRequest;
import lifeplanner.web.dto.EditGoalRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceUTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalLikesService goalLikesService;

    @Mock
    private GoalFavoriteService goalFavoriteService;

    @InjectMocks
    private GoalService goalService;

    @Test
    void testGetGoalsByUserComputesProgressBar() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Goal goal = new Goal();
        goal.setProgress(50);
        // Other goal properties can be set as needed.

        when(goalRepository.findAllByOwner(user)).thenReturn(List.of(goal));

        List<Goal> result = goalService.getGoalsByUser(user);

        // Compute expected progress bar
        int originalProgress = 50;
        int milestoneCount = (int) Math.floor((originalProgress / 100.0) * 10);
        String expectedBar = ProgressUtil.computeProgressBar(milestoneCount, 10, originalProgress);

        assertEquals(expectedBar, result.get(0).getProgressBar());
    }

    @Test
    void testAddGoal() {
        User user = new User();
        user.setId(UUID.randomUUID());

        AddGoalRequest request = new AddGoalRequest();
        request.setGoalName("Learn Java");
        request.setCategory(GoalCategory.FINANCE);
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 12, 31));
        request.setPriority(GoalPriority.HIGH);
        request.setProgress(20);
        request.setStatus(GoalStatus.IN_PROGRESS);
        request.setNotes("Read chapters daily");

        goalService.addGoal(request, user);

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(captor.capture());
        Goal savedGoal = captor.getValue();

        assertEquals("Learn Java", savedGoal.getGoalName());
        assertEquals(GoalCategory.FINANCE, savedGoal.getCategory());
        assertEquals(LocalDate.of(2025, 1, 1), savedGoal.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), savedGoal.getEndDate());
        assertEquals(GoalPriority.HIGH, savedGoal.getPriority());
        assertEquals(20, savedGoal.getProgress());
        assertEquals(GoalStatus.IN_PROGRESS, savedGoal.getStatus());
        assertEquals("Read chapters daily", savedGoal.getNotes());
        assertFalse(savedGoal.isVisible());
        assertEquals(ApprovalStatus.PENDING, savedGoal.getApprovalStatus());
        assertEquals(user, savedGoal.getOwner());
    }

    @Test
    void testGetGoalByIdNotFound() {
        UUID goalId = UUID.randomUUID();
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        assertThrows(GoalNotFoundException.class, () -> goalService.getGoalById(goalId));
    }

    @Test
    void testGetGoalByIdFound() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        Goal result = goalService.getGoalById(goalId);
        assertEquals(goalId, result.getId());
    }

    @Test
    void testEditGoal() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setGoalName("Old Name");
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        EditGoalRequest editRequest = EditGoalRequest.builder().build();
        editRequest.setGoalName("New Name");
        editRequest.setCategory(GoalCategory.RELATIONSHIPS);
        editRequest.setStartDate(LocalDate.of(2025, 3, 1));
        editRequest.setEndDate(LocalDate.of(2025, 3, 31));
        editRequest.setPriority(GoalPriority.MEDIUM);
        editRequest.setProgress(70);
        editRequest.setStatus(GoalStatus.COMPLETED);
        editRequest.setNotes("Updated notes");

        goalService.editGoal(goalId, editRequest);

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(captor.capture());
        Goal updatedGoal = captor.getValue();

        assertEquals("New Name", updatedGoal.getGoalName());
        assertEquals(GoalCategory.RELATIONSHIPS, updatedGoal.getCategory());
        assertEquals(LocalDate.of(2025, 3, 1), updatedGoal.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 31), updatedGoal.getEndDate());
        assertEquals(GoalPriority.MEDIUM, updatedGoal.getPriority());
        assertEquals(70, updatedGoal.getProgress());
        assertEquals(GoalStatus.COMPLETED, updatedGoal.getStatus());
        assertEquals("Updated notes", updatedGoal.getNotes());
    }

    @Test
    void testShareGoalNotFound() {
        UUID goalId = UUID.randomUUID();
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        assertThrows(GoalNotFoundException.class, () -> goalService.shareGoal(goalId));
    }

    @Test
    void testShareGoalAlreadyShared() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setVisible(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        assertThrows(GoalAlreadySharedException.class, () -> goalService.shareGoal(goalId));
    }

    @Test
    void testShareGoalRejected() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.REJECTED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        assertThrows(GoalRejectedException.class, () -> goalService.shareGoal(goalId));
    }

    @Test
    void testShareGoalPending() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.PENDING);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        assertThrows(GoalPendingApprovalException.class, () -> goalService.shareGoal(goalId));
    }

    @Test
    void testShareGoalApproved() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.APPROVED);
        goal.setVisible(false);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        goalService.shareGoal(goalId);
        assertTrue(goal.isVisible());
        verify(goalRepository).save(goal);
    }

    @Test
    void testGetAllGoals() {
        List<Goal> goals = List.of(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);
        List<Goal> result = goalService.getAllGoals();
        assertEquals(2, result.size());
    }

    @Test
    void testGetLikeCountsForGoals() {
        Goal goal1 = new Goal();
        UUID id1 = UUID.randomUUID();
        goal1.setId(id1);
        Goal goal2 = new Goal();
        UUID id2 = UUID.randomUUID();
        goal2.setId(id2);

        when(goalLikesService.getLikeCount(id1)).thenReturn(10L);
        when(goalLikesService.getLikeCount(id2)).thenReturn(5L);

        Map<UUID, Long> counts = goalService.getLikeCountsForGoals(List.of(goal1, goal2));
        assertEquals(10L, counts.get(id1));
        assertEquals(5L, counts.get(id2));
    }

    @Test
    void testGetFavoriteCountsForGoals() {
        Goal goal1 = new Goal();
        UUID id1 = UUID.randomUUID();
        goal1.setId(id1);
        Goal goal2 = new Goal();
        UUID id2 = UUID.randomUUID();
        goal2.setId(id2);

        when(goalFavoriteService.getFavoriteCount(id1)).thenReturn(3L);
        when(goalFavoriteService.getFavoriteCount(id2)).thenReturn(7L);

        Map<UUID, Long> counts = goalService.getFavoriteCountsForGoals(List.of(goal1, goal2));
        assertEquals(3L, counts.get(id1));
        assertEquals(7L, counts.get(id2));
    }

    @Test
    void testGetApprovedSharedGoals() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Goal goal1 = new Goal();
        goal1.setId(UUID.randomUUID());
        User owner1 = new User();
        owner1.setId(UUID.randomUUID());
        goal1.setOwner(owner1);
        goal1.setApprovalStatus(ApprovalStatus.APPROVED);
        goal1.setVisible(true);

        // This goal belongs to currentUser and should be filtered out.
        Goal goal2 = new Goal();
        goal2.setId(UUID.randomUUID());
        goal2.setOwner(currentUser);
        goal2.setApprovalStatus(ApprovalStatus.APPROVED);
        goal2.setVisible(true);

        when(goalRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED))
                .thenReturn(List.of(goal1, goal2));

        List<Goal> result = goalService.getApprovedSharedGoals(currentUser);
        assertEquals(1, result.size());
        assertTrue(result.contains(goal1));
    }

    @Test
    void testGetMySharedGoals() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Goal goal1 = new Goal();
        goal1.setId(UUID.randomUUID());
        goal1.setOwner(currentUser);
        goal1.setVisible(true);

        // Goal with a different owner should be filtered out.
        Goal goal2 = new Goal();
        goal2.setId(UUID.randomUUID());
        User otherOwner = new User();
        otherOwner.setId(UUID.randomUUID());
        goal2.setOwner(otherOwner);
        goal2.setVisible(true);

        when(goalRepository.findAllByVisibleTrue())
                .thenReturn(List.of(goal1, goal2));

        List<Goal> result = goalService.getMySharedGoals(currentUser);
        assertEquals(1, result.size());
        assertTrue(result.contains(goal1));
    }

    @Test
    void testRemoveSharingNotFound() {
        UUID goalId = UUID.randomUUID();
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());
        assertThrows(GoalNotFoundException.class, () -> goalService.removeSharing(goalId));
    }

    @Test
    void testRemoveSharing() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setVisible(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        goalService.removeSharing(goalId);
        assertFalse(goal.isVisible());
        verify(goalRepository).save(goal);
    }

    @Test
    void testDeleteGoalById() {
        UUID goalId = UUID.randomUUID();
        goalService.deleteGoalById(goalId);
        verify(goalRepository).deleteById(goalId);
    }

    @Test
    void testGetPendingGoals() {
        Goal goal1 = new Goal();
        goal1.setApprovalStatus(ApprovalStatus.PENDING);
        Goal goal2 = new Goal();
        goal2.setApprovalStatus(ApprovalStatus.PENDING);
        when(goalRepository.findAllByApprovalStatus(ApprovalStatus.PENDING))
                .thenReturn(List.of(goal1, goal2));

        List<Goal> result = goalService.getPendingGoals();
        assertEquals(2, result.size());
    }

    @Test
    void testApproveGoalAlreadyApproved() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.APPROVED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        assertThrows(GoalAlreadyApprovedException.class, () -> goalService.approveGoal(goalId));
    }

    @Test
    void testApproveGoal() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.PENDING);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        goalService.approveGoal(goalId);
        assertEquals(ApprovalStatus.APPROVED, goal.getApprovalStatus());
        verify(goalRepository).save(goal);
    }

    @Test
    void testRejectGoalAlreadyRejected() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.REJECTED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        assertThrows(GoalAlreadyRejectedException.class, () -> goalService.rejectGoal(goalId));
    }

    @Test
    void testRejectGoal() {
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setApprovalStatus(ApprovalStatus.PENDING);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        goalService.rejectGoal(goalId);
        assertEquals(ApprovalStatus.REJECTED, goal.getApprovalStatus());
        verify(goalRepository).save(goal);
    }
}