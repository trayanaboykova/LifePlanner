package lifeplanner.web;

import jakarta.validation.Valid;
import lifeplanner.quotes.client.dto.AddDailyQuoteRequest;
import lifeplanner.quotes.client.dto.DailyQuote;
import lifeplanner.quotes.client.dto.EditDailyQuotesRequest;
import lifeplanner.quotes.service.DailyQuoteService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("daily-quotes")
public class DailyQuotesController {

    private final UserService userService;
    private final DailyQuoteService dailyQuoteService;

    @Autowired
    public DailyQuotesController(UserService userService, DailyQuoteService dailyQuoteService) {
        this.userService = userService;
        this.dailyQuoteService = dailyQuoteService;
    }

    @GetMapping
    public ModelAndView showDailyQuotesPage(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "Daily Quotes");
        User user = userService.getById(authenticationMetadata.getUserId());
        List<DailyQuote> quotes = dailyQuoteService.getQuotesByUserId(user.getId());
        model.addAttribute("quotes", quotes);
        return new ModelAndView("daily-quotes");
    }

    @GetMapping("/new")
    public ModelAndView showAddQuoteRequest(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "Add Daily Quote");
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-daily-quote");
        modelAndView.addObject("user", user);
        model.addAttribute("addDailyQuoteRequest", new AddDailyQuoteRequest());
        return modelAndView;
    }

    @PostMapping
    public String addDailyQuote(@Valid @ModelAttribute("addDailyQuoteRequest") AddDailyQuoteRequest addDailyQuoteRequest,
                                BindingResult bindingResult,
                                Model model,
                                @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "Add Daily Quote");
        if (bindingResult.hasErrors()) {
            return "add-daily-quote";
        }
        User user = userService.getById(authenticationMetadata.getUserId());
        dailyQuoteService.addDailyQuote(addDailyQuoteRequest, user);
        return "redirect:/daily-quotes";
    }

    // GET mapping to display the edit form
    @GetMapping("/{id}/edit")
    public ModelAndView showEditQuoteForm(@PathVariable UUID id, Model model) {
        DailyQuote existingQuote = dailyQuoteService.getQuoteById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));
        // Create an Edit DTO from the existing quote
        EditDailyQuotesRequest editRequest = EditDailyQuotesRequest.builder()
                .id(existingQuote.getId())
                .quoteImage(existingQuote.getQuoteImage())
                .userId(existingQuote.getUserId()) // carry the userId
                .build();
        model.addAttribute("pageTitle", "Edit Daily Quote");
        model.addAttribute("editDailyQuoteRequest", editRequest);
        return new ModelAndView("edit-daily-quote");
    }

    // POST mapping to handle edit form submission
    @PostMapping("/{id}/edit")
    public String updateDailyQuote(@PathVariable UUID id,
                                   @Valid @ModelAttribute("editDailyQuoteRequest") EditDailyQuotesRequest editRequest,
                                   BindingResult bindingResult,
                                   Model model) {
        model.addAttribute("pageTitle", "Edit Daily Quote");
        if (bindingResult.hasErrors()) {
            return "edit-daily-quote";
        }
        dailyQuoteService.updateDailyQuote(id, editRequest); // You may need to add this overloaded method in your LifePlanner service that calls the Feign client
        return "redirect:/daily-quotes";
    }

    // Handle deletion of a daily quote
    @DeleteMapping("/delete/{id}")
    public String deleteDailyQuote(@PathVariable UUID id) {
        dailyQuoteService.deleteDailyQuote(id);
        return "redirect:/daily-quotes";
    }
}