package lifeplanner.goals.service;

import lifeplanner.exception.goals.*;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.util.ProgressUtil;
import lifeplanner.web.dto.AddGoalRequest;
import lifeplanner.web.dto.EditGoalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalLikesService goalLikesService;
    private final GoalFavoriteService goalFavoriteService;

    @Autowired
    public GoalService(GoalRepository goalRepository, GoalLikesService goalLikesService, GoalFavoriteService goalFavoriteService) {
        this.goalRepository = goalRepository;
        this.goalLikesService = goalLikesService;
        this.goalFavoriteService = goalFavoriteService;
    }

    public List<Goal> getGoalsByUser(User user) {
        List<Goal> goals = goalRepository.findAllByOwner(user);

        for (Goal goal : goals) {
            int originalProgress = (goal.getProgress() != null) ? goal.getProgress() : 0;
            int milestoneCount = (int) Math.floor((originalProgress / 100.0) * 10);
            String bar = ProgressUtil.computeProgressBar(milestoneCount, 10, originalProgress);
            goal.setProgressBar(bar);
        }
        return goals;
    }

    @Transactional
    public void addGoal(AddGoalRequest addGoalRequest, User user) {
        Goal goal = Goal.builder()
                .goalName(addGoalRequest.getGoalName())
                .category(addGoalRequest.getCategory())
                .startDate(addGoalRequest.getStartDate())
                .endDate(addGoalRequest.getEndDate())
                .priority(addGoalRequest.getPriority())
                .progress(addGoalRequest.getProgress())
                .status(addGoalRequest.getStatus())
                .notes(addGoalRequest.getNotes())
                .owner(user)
                .visible(false)
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        goalRepository.save(goal);
    }

    public Goal getGoalById(UUID goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));
    }

    @Transactional
    public void editGoal(UUID goalId, EditGoalRequest editGoalRequest) {
        Goal goal = getGoalById(goalId);
        goal.setGoalName(editGoalRequest.getGoalName());
        goal.setCategory(editGoalRequest.getCategory());
        goal.setStartDate(editGoalRequest.getStartDate());
        goal.setEndDate(editGoalRequest.getEndDate());
        goal.setPriority(editGoalRequest.getPriority());
        goal.setProgress(editGoalRequest.getProgress());
        goal.setStatus(editGoalRequest.getStatus());
        goal.setNotes(editGoalRequest.getNotes());

        goalRepository.save(goal);
    }

    @Transactional
    public void shareGoal(UUID goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        if (goal.isVisible()) {
            throw new GoalAlreadySharedException(goalId);
        }

        if (goal.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new GoalRejectedException(goalId);
        }

        if (goal.getApprovalStatus() == ApprovalStatus.PENDING) {
            throw new GoalPendingApprovalException(goalId);
        }

        goal.setVisible(true);
        goalRepository.save(goal);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Map<UUID, Long> getLikeCountsForGoals(List<Goal> goals) {
        return goals.stream()
                .collect(Collectors.toMap(
                        Goal::getId,
                        goal -> goalLikesService.getLikeCount(goal.getId())
                ));
    }

    public Map<UUID, Long> getFavoriteCountsForGoals(List<Goal> goals) {
        return goals.stream()
                .collect(Collectors.toMap(
                        Goal::getId,
                        goal -> goalFavoriteService.getFavoriteCount(goal.getId())
                ));
    }

    public List<Goal> getApprovedSharedGoals(User currentUser) {
        List<Goal> approvedGoals = goalRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED);
        return approvedGoals
                .stream()
                .filter(goal -> !goal.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Goal> getMySharedGoals(User currentUser) {
        return goalRepository.findAllByVisibleTrue()
                .stream()
                .filter(goal -> goal.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    @Transactional
    public void removeSharing(UUID goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));
        goal.setVisible(false);
        goalRepository.save(goal);
    }

    @Transactional
    public void deleteGoalById(UUID id) {
        goalRepository.deleteById(id);
    }

    public List<Goal> getPendingGoals() {
        return goalRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Transactional
    public void approveGoal(UUID goalId) {
        Goal goal = getGoalById(goalId);

        if (goal.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new GoalAlreadyApprovedException(goalId);
        }

        goal.setApprovalStatus(ApprovalStatus.APPROVED);
        goalRepository.save(goal);
    }

    @Transactional
    public void rejectGoal(UUID goalId) {
        Goal goal = getGoalById(goalId);

        if (goal.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new GoalAlreadyRejectedException(goalId);
        }

        goal.setApprovalStatus(ApprovalStatus.REJECTED);
        goalRepository.save(goal);
    }
}