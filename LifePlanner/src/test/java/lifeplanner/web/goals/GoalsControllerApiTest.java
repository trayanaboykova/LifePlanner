package lifeplanner.web.goals;

import lifeplanner.goals.model.Goal;
import lifeplanner.goals.service.GoalService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import lifeplanner.web.dto.AddGoalRequest;
import lifeplanner.web.dto.EditGoalRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalsController.class)
public class GoalsControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalService goalService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testGoalId = UUID.randomUUID();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(testUserId, "user", "password", UserRole.USER, true);
    }

    private User createDummyUser() {
        User user = new User();
        user.setId(testUserId);
        user.setUsername("user");
        return user;
    }

    private Goal createDummyGoal() {
        Goal goal = new Goal();
        goal.setId(testGoalId);
        goal.setGoalName("Test Goal");
        // Set additional fields if needed
        return goal;
    }

    @Test
    void getMyGoalsPage_ShouldReturnMyGoalsView() throws Exception {
        User dummyUser = createDummyUser();
        List<Goal> goals = Collections.singletonList(createDummyGoal());
        when(userService.getById(testUserId)).thenReturn(dummyUser);
        when(goalService.getGoalsByUser(dummyUser)).thenReturn(goals);

        mockMvc.perform(get("/goals/my-goals")
                        .with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("my-goals"))
                .andExpect(model().attribute("pageTitle", "Goals"))
                .andExpect(model().attribute("user", dummyUser))
                .andExpect(model().attribute("goals", goals));
    }

    @Test
    void getAllGoalsPage_ShouldReturnAllGoalsView() throws Exception {
        User dummyUser = createDummyUser();
        List<Goal> goals = Collections.singletonList(createDummyGoal());
        when(userService.getById(testUserId)).thenReturn(dummyUser);
        when(goalService.getGoalsByUser(dummyUser)).thenReturn(goals);

        mockMvc.perform(get("/goals/all-goals")
                        .with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-goals"))
                .andExpect(model().attribute("pageTitle", "All Goals"))
                .andExpect(model().attribute("goals", goals));
    }

    @Test
    void getCompletedGoalsPage_ShouldReturnCompletedGoalsView() throws Exception {
        User dummyUser = createDummyUser();
        List<Goal> goals = Collections.singletonList(createDummyGoal());
        when(userService.getById(testUserId)).thenReturn(dummyUser);
        when(goalService.getGoalsByUser(dummyUser)).thenReturn(goals);

        mockMvc.perform(get("/goals/completed-goals")
                        .with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("completed-goals"))
                .andExpect(model().attribute("pageTitle", "Completed Goals"))
                .andExpect(model().attribute("goals", goals));
    }

    @Test
    void showAddGoalRequest_ShouldReturnAddGoalView() throws Exception {
        User dummyUser = createDummyUser();
        when(userService.getById(testUserId)).thenReturn(dummyUser);

        mockMvc.perform(get("/goals/new")
                        .with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-goal"))
                .andExpect(model().attribute("pageTitle", "Add Goal"))
                .andExpect(model().attribute("user", dummyUser))
                .andExpect(model().attributeExists("addGoalRequest"));
    }

    @Test
    void addGoal_WithValidRequest_ShouldRedirect() throws Exception {
        User dummyUser = createDummyUser();
        when(userService.getById(testUserId)).thenReturn(dummyUser);

        mockMvc.perform(post("/goals")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("goalName", "New Goal")
                        .param("status", "NOT_STARTED")
                        .param("description", "Goal Description")
                        .param("category", "HEALTH_AND_FITNESS")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                        .param("priority", "MEDIUM")
                        .param("progress", "0")
                        .param("notes", "Some notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals/all-goals"));

        verify(goalService).addGoal(any(AddGoalRequest.class), eq(dummyUser));
    }

    @Test
    void addGoal_WithInvalidRequest_ShouldReturnAddGoalForm() throws Exception {
        User dummyUser = createDummyUser();
        when(userService.getById(testUserId)).thenReturn(dummyUser);

        mockMvc.perform(post("/goals")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("status", "NOT_STARTED")
                        .param("description", "Goal Description")
                        .param("category", "HEALTH_AND_FITNESS")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                        .param("priority", "MEDIUM")
                        .param("progress", "0")
                        .param("notes", "Some notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-goal"));
    }


    @Test
    void showEditGoalRequest_ShouldReturnEditGoalView() throws Exception {
        Goal dummyGoal = createDummyGoal();
        when(goalService.getGoalById(testGoalId)).thenReturn(dummyGoal);

        mockMvc.perform(get("/goals/{id}/edit", testGoalId)
                        .with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-goal"))
                .andExpect(model().attributeExists("pageTitle", "goal", "editGoalRequest"));
    }

    @Test
    void updateGoal_WithValidRequest_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/goals/{id}/edit", testGoalId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("goalName", "Updated Goal")
                        .param("status", "IN_PROGRESS")
                        .param("description", "Updated Description")
                        .param("category", "HEALTH_AND_FITNESS")
                        .param("startDate", "2025-02-01")
                        .param("endDate", "2025-11-30")
                        .param("priority", "HIGH")
                        .param("progress", "50")
                        .param("notes", "Updated notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals/all-goals"));

        verify(goalService).editGoal(eq(testGoalId), any(EditGoalRequest.class));
    }

    @Test
    void updateGoal_WithBindingErrors_ShouldReturnEditView() throws Exception {
        Goal dummyGoal = createDummyGoal();
        when(goalService.getGoalById(testGoalId)).thenReturn(dummyGoal);

        mockMvc.perform(post("/goals/{id}/edit", testGoalId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("goalName", "")
                        .param("status", "IN_PROGRESS")
                        .param("description", "Updated Description"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-goal"))
                .andExpect(model().attributeExists("goal", "editGoalRequest"));
    }

    @Test
    void shareGoal_ShouldRedirectToMyGoals() throws Exception {
        mockMvc.perform(post("/goals/{id}/share", testGoalId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals/my-goals"));

        verify(goalService).shareGoal(testGoalId);
    }

    @Test
    void removeSharing_ShouldRedirectToMySharedPosts() throws Exception {
        mockMvc.perform(post("/goals/{id}/remove", testGoalId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-shared-posts"));

        verify(goalService).removeSharing(testGoalId);
    }

    @Test
    void deleteGoal_ShouldRedirectToMyGoals() throws Exception {
        mockMvc.perform(delete("/goals/{id}", testGoalId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals/my-goals"));

        verify(goalService).deleteGoalById(testGoalId);
    }
}