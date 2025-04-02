package lifeplanner.schedulers;

import lifeplanner.quotes.client.dto.DailyQuote;
import lifeplanner.quotes.service.DailyQuoteService;
import lifeplanner.scheduler.DailyQuotesScheduler;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class DailyQuotesSchedulerUTest {

    private UserService userService;
    private DailyQuoteService dailyQuoteService;
    private DailyQuotesScheduler scheduler;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        dailyQuoteService = mock(DailyQuoteService.class);
        scheduler = new DailyQuotesScheduler(userService, dailyQuoteService);
    }

    @Test
    void updateUsersDailyQuote_withQuotes_updatesUserDailyQuote() {
        // Create a test user.
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");

        // Create daily quotes for the user.
        DailyQuote quote1 = new DailyQuote();
        quote1.setQuoteImage("image1.png");
        DailyQuote quote2 = new DailyQuote();
        quote2.setQuoteImage("image2.png");
        List<DailyQuote> quotes = Arrays.asList(quote1, quote2);

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        when(dailyQuoteService.getQuotesByUserId(user.getId())).thenReturn(quotes);

        scheduler.updateUsersDailyQuote();

        // Verify that updateUser is called with a user whose currentQuoteImageUrl is one of the provided quotes.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateUser(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertNotNull(updatedUser.getCurrentQuoteImageUrl());
        assertTrue(updatedUser.getCurrentQuoteImageUrl().equals("image1.png")
                || updatedUser.getCurrentQuoteImageUrl().equals("image2.png"));
    }

    @Test
    void updateUsersDailyQuote_withoutQuotes_doesNotUpdateUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        when(dailyQuoteService.getQuotesByUserId(user.getId())).thenReturn(Collections.emptyList());

        scheduler.updateUsersDailyQuote();

        verify(userService, never()).updateUser(any(User.class));
    }
}