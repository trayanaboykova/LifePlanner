package lifeplanner.quotes.service;

import lifeplanner.quotes.client.DailyQuotesClient;
import lifeplanner.quotes.client.dto.AddDailyQuoteRequest;
import lifeplanner.quotes.client.dto.DailyQuote;
import lifeplanner.quotes.client.dto.EditDailyQuotesRequest;
import lifeplanner.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DailyQuoteService {
    private final DailyQuotesClient dailyQuotesClient;

    @Autowired
    public DailyQuoteService(DailyQuotesClient dailyQuotesClient) {
        this.dailyQuotesClient = dailyQuotesClient;
    }

    // Add a new daily quote
    public DailyQuote addDailyQuote(AddDailyQuoteRequest addDailyQuoteRequest, User user) {
        DailyQuote dailyQuote = new DailyQuote();
        dailyQuote.setQuoteImage(addDailyQuoteRequest.getQuoteImage());
        dailyQuote.setUserId(user.getId());
        return dailyQuotesClient.addDailyQuote(dailyQuote);
    }

    public List<DailyQuote> getQuotesByUserId(UUID userId) {
        return dailyQuotesClient.getQuotesByUserId(userId);
    }

    // Retrieve a specific daily quote by ID
    public Optional<DailyQuote> getQuoteById(UUID id) {
        return Optional.ofNullable(dailyQuotesClient.getQuoteById(id));
    }

    // Update an existing daily quote
    public DailyQuote updateDailyQuote(UUID id, EditDailyQuotesRequest editRequest) {
        // Create a DailyQuote DTO to send via Feign
        DailyQuote dailyQuote = DailyQuote.builder()
                .id(id)
                .quoteImage(editRequest.getQuoteImage())
                .userId(editRequest.getUserId())
                .build();
        return dailyQuotesClient.updateDailyQuote(id, dailyQuote);
    }

    // Delete a daily quote by ID
    public void deleteDailyQuote(UUID id) {
        dailyQuotesClient.deleteDailyQuote(id);
    }
}