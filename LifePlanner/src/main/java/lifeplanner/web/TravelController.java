package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.service.TravelService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddTripRequest;
import lifeplanner.web.dto.EditTripRequest;
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
@RequestMapping("/trips")
public class TravelController {

    private final TravelService travelService;
    private final UserService userService;

    @Autowired
    public TravelController(TravelService travelService, UserService userService) {
        this.travelService = travelService;
        this.userService = userService;
    }

    @GetMapping("/travel")
    public String getTravelPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Travel Plans");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Travel> userTrips = travelService.getTripsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("trips", userTrips);

        return "travel";
    }

    @GetMapping("/all-trips")
    public String getAllTripsPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "All Trips");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Travel> userTrips = travelService.getTripsByUser(user);

        model.addAttribute("trips", userTrips);

        return "all-trips";
    }

    @GetMapping("/past-trips")
    public String getPastTripsPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Past Trips");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Travel> userTrips = travelService.getTripsByUser(user);

        model.addAttribute("trips", userTrips);

        return "past-trips";
    }

    @GetMapping("/new")
    public ModelAndView showAddTravelRequest(HttpSession session, Model model) {

        model.addAttribute("pageTitle", "Add Trip");

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-trip");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addTripRequest", new AddTripRequest());

        return modelAndView;
    }

    @PostMapping
    public String addTrip(@Valid AddTripRequest addTripRequest,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model) {

        model.addAttribute("pageTitle", "Add Trip");

        if (bindingResult.hasErrors()) {
            return "add-trip";
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        travelService.addTrip(addTripRequest, user);

        return "redirect:/trips/travel";
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditTripRequest(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("pageTitle", "Edit Trip");

        Travel trip = travelService.getTripById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-trip");
        modelAndView.addObject("trip", trip);
        modelAndView.addObject("editTravelRequest", DTOMapper.mapBookToEditTravelRequest(trip));
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateTrip(@PathVariable("id") UUID id,
                                   @Valid @ModelAttribute("editTripRequest") EditTripRequest editTripRequest,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Travel trip = travelService.getTripById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-trip");
            modelAndView.addObject("trip", trip);
            modelAndView.addObject("editTravelRequest", DTOMapper.mapBookToEditTravelRequest(trip));
            return modelAndView;
        }

        travelService.editTrip(id, editTripRequest);
        return new ModelAndView("redirect:/trips/travel");
    }

    @PostMapping("/{id}/share")
    public String shareTrip(@PathVariable UUID id) {

        travelService.shareTrip(id);

        return "redirect:/trips/travel";
    }


    @DeleteMapping("/{id}")
    public String deleteTrip(@PathVariable UUID id) {

        travelService.deleteTripById(id);

        return "redirect:/trips/travel";
    }

}
