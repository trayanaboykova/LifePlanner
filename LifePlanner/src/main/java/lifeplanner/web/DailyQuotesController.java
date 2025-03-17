package lifeplanner.web;

import lifeplanner.quotes.service.DailyQuoteService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String showDailyQuotesPage(Model model, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        model.addAttribute("pageTitle", "Daily Quotes");
        return "daily-quotes";
    }
}