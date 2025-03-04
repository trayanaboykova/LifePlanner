package lifeplanner.web.goals;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.service.GoalService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddGoalRequest;
import lifeplanner.web.dto.EditGoalRequest;
import lifeplanner.web.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("goals")
public class GoalsController {

    private final GoalService goalService;
    private final UserService userService;

    @Autowired
    public GoalsController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping("/my-goals")
    public String getMyGoalsPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Goals");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Goal> userGoals = goalService.getGoalsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("goals", userGoals);

        return "my-goals";
    }

    @GetMapping("/all-goals")
    public String getAllGoalsPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "All Goals");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Goal> userGoals = goalService.getGoalsByUser(user);

        model.addAttribute("goals", userGoals);

        return "all-goals";
    }

    @GetMapping("/completed-goals")
    public String getCompletedGoalsPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Completed Goals");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Goal> userGoals = goalService.getGoalsByUser(user);

        model.addAttribute("goals", userGoals);

        return "completed-goals";
    }

    @GetMapping("/new")
    public ModelAndView showAddGoalRequest(HttpSession session, Model model) {

        model.addAttribute("pageTitle", "Add Goal");

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-goal");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addGoalRequest", new AddGoalRequest());

        return modelAndView;
    }

    @PostMapping
    public String addGoal(@Valid AddGoalRequest addGoalRequest,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model) {

        model.addAttribute("pageTitle", "Add Goal");

        if (bindingResult.hasErrors()) {
            return "add-goal";
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        goalService.addGoal(addGoalRequest, user);

        return "redirect:/goals/all-goals";
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditGoalRequest(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("pageTitle", "Edit Goal");

        Goal goal = goalService.getGoalById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-goal");
        modelAndView.addObject("goal", goal);
        modelAndView.addObject("editGoalRequest", DTOMapper.mapBookToEditGoalRequest(goal));
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateGoal(@PathVariable("id") UUID id,
                                   @Valid @ModelAttribute("editGoalRequest") EditGoalRequest editGoalRequest,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Goal goal = goalService.getGoalById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-goal");
            modelAndView.addObject("goal", goal);
            modelAndView.addObject("editGoalRequest", DTOMapper.mapBookToEditGoalRequest(goal));
            return modelAndView;
        }

        goalService.editGoal(id, editGoalRequest);
        return new ModelAndView("redirect:/goals/all-goals");
    }

    @PostMapping("/{id}/share")
    public String shareGoal(@PathVariable UUID id) {

        goalService.shareGoal(id);

        return "redirect:/goals/my-goals";
    }


    @DeleteMapping("/{id}")
    public String deleteGoal(@PathVariable UUID id) {

        goalService.deleteGoalById(id);

        return "redirect:/goals/my-goals";
    }
}
