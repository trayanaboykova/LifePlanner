package lifeplanner.scheduler;

import lifeplanner.quotes.client.dto.DailyQuote;
import lifeplanner.quotes.service.DailyQuoteService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class DailyQuotesScheduler {

    private final UserService userService;
    private final DailyQuoteService dailyQuoteService;
    private final Random random = new Random();

    @Autowired
    public DailyQuotesScheduler(UserService userService, DailyQuoteService dailyQuoteService) {
        this.userService = userService;
        this.dailyQuoteService = dailyQuoteService;
    }

    // Run every 30 minutes
    @Scheduled(cron = "15 * * * * *")
    public void updateUsersDailyQuote() {
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            List<DailyQuote> userQuotes = dailyQuoteService.getQuotesByUserId(user.getId());
            if (!userQuotes.isEmpty()) {
                DailyQuote selectedQuote = userQuotes.get(random.nextInt(userQuotes.size()));
                user.setCurrentQuoteImageUrl(selectedQuote.getQuoteImage());
                userService.updateUser(user);
                log.info("Updated daily quote for user {} to {}", user.getUsername(), user.getCurrentQuoteImageUrl());
            } else {
                log.info("No quotes found for user {}", user.getUsername());
            }
        }
    }
}