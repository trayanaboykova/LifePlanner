package lifeplanner.goals.service;

import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.repository.GoalRepository;
import lifeplanner.user.model.User;
import lifeplanner.util.ProgressUtil;
import lifeplanner.web.dto.AddGoalRequest;
import lifeplanner.web.dto.EditGoalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
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
                .build();

        goalRepository.save(goal);
    }

    public Goal getGoalById(UUID goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal with id [" + goalId + "] does not exist."));
    }

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

    public void shareGoal(UUID goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setVisible(true);
        goalRepository.save(goal);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public List<Goal> getSharedGoals(User currentUser) {
        return goalRepository.findAllByVisibleTrue()
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

    public void removeSharing(UUID goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setVisible(false);
        goalRepository.save(goal);
    }

    public void deleteGoalById(UUID id) {
        goalRepository.deleteById(id);
    }

    public List<Goal> getPendingGoals() {
        return goalRepository.findAllByApprovedFalse();
    }
}
