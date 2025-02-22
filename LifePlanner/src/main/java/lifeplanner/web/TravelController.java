package lifeplanner.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TravelController {

    @GetMapping("/travel")
    public String getTravelPage(Model model) {
        model.addAttribute("pageTitle", "Travel Plans");
        return "travel";
    }

    @GetMapping("/all-trips")
    public String getAllTripsPage(Model model) {
        model.addAttribute("pageTitle", "All Trips");
        return "all-trips";
    }

    @GetMapping("/past-trips")
    public String getPastTripsPage(Model model) {
        model.addAttribute("pageTitle", "Past Trips");
        return "past-trips";
    }

    @GetMapping("/add-trip")
    public String getAddTripPage(Model model) {
        model.addAttribute("pageTitle", "Add Trip");
        return "add-trip";
    }

    @GetMapping("/edit-trip")
    public String getEditTripPage(Model model) {
        model.addAttribute("pageTitle", "Edit Trip");
        return "edit-trip";
    }
}
