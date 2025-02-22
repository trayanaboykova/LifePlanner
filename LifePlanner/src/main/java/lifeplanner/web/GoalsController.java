package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoalsController {

    @GetMapping("/goals")
    public String getGoalsPage(Model model) {
        model.addAttribute("pageTitle", "Goals");
        return "goals";
    }

    @GetMapping("/add-goal")
    public String getAddGoalPage(Model model) {
        model.addAttribute("pageTitle", "Add Goal");
        return "add-goal";
    }

    @GetMapping("/edit-goal")
    public String getEditGoalPage(Model model) {
        model.addAttribute("pageTitle", "Edit Goal");
        return "edit-goal";
    }

    @GetMapping("/all-goals")
    public String getAllGoalsPage(Model model) {
        model.addAttribute("pageTitle", "All Goals");
        return "all-goals";
    }

    @GetMapping("/completed-goals")
    public String getCompletedGoalsPage(Model model) {
        model.addAttribute("pageTitle", "Completed Goals");
        return "completed-goals";
    }
}
