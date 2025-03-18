package lifeplanner.quotes.client;

import lifeplanner.quotes.client.dto.DailyQuote;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "daily-quotes-service",
        url = "http://localhost:8081/api/v1/daily-quotes")
public interface DailyQuotesClient {

    @GetMapping("/user/{userId}")
    List<DailyQuote> getQuotesByUserId(@PathVariable UUID userId);

    @GetMapping("/{id}")
    DailyQuote getQuoteById(@PathVariable UUID id);

    @PostMapping
    DailyQuote addDailyQuote(@RequestBody DailyQuote dailyQuote);

    @PutMapping("/{id}")
    DailyQuote updateDailyQuote(@PathVariable UUID id, @RequestBody DailyQuote dailyQuote);

    @DeleteMapping("/{id}")
    void deleteDailyQuote(@PathVariable UUID id);

}