package lifeplanner.quotes;

import lifeplanner.quotes.client.DailyQuotesClient;
import lifeplanner.quotes.client.dto.AddDailyQuoteRequest;
import lifeplanner.quotes.client.dto.DailyQuote;
import lifeplanner.quotes.client.dto.EditDailyQuotesRequest;
import lifeplanner.quotes.service.DailyQuoteService;
import lifeplanner.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyQuoteServiceUTest {

    @Mock
    private DailyQuotesClient dailyQuotesClient;

    private DailyQuoteService dailyQuoteService;

    @BeforeEach
    void setUp() {
        dailyQuoteService = new DailyQuoteService(dailyQuotesClient);
    }

    @Test
    void addDailyQuote_ShouldReturnCreatedQuote() {
        // Arrange
        AddDailyQuoteRequest request = new AddDailyQuoteRequest();
        request.setQuoteImage("test-image-url");

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        DailyQuote expectedQuote = new DailyQuote();
        expectedQuote.setQuoteImage("test-image-url");
        expectedQuote.setUserId(userId);

        when(dailyQuotesClient.addDailyQuote(any(DailyQuote.class))).thenReturn(expectedQuote);

        // Act
        DailyQuote result = dailyQuoteService.addDailyQuote(request, user);

        // Assert
        assertNotNull(result);
        assertEquals("test-image-url", result.getQuoteImage());
        assertEquals(userId, result.getUserId());
        verify(dailyQuotesClient).addDailyQuote(any(DailyQuote.class));
    }

    @Test
    void getQuotesByUserId_ShouldReturnQuotesList() {
        // Arrange
        UUID userId = UUID.randomUUID();
        DailyQuote quote1 = new DailyQuote();
        DailyQuote quote2 = new DailyQuote();
        List<DailyQuote> expectedQuotes = List.of(quote1, quote2);

        when(dailyQuotesClient.getQuotesByUserId(userId)).thenReturn(expectedQuotes);

        // Act
        List<DailyQuote> result = dailyQuoteService.getQuotesByUserId(userId);

        // Assert
        assertEquals(2, result.size());
        verify(dailyQuotesClient).getQuotesByUserId(userId);
    }

    @Test
    void getQuoteById_ShouldReturnQuoteWhenExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        DailyQuote expectedQuote = new DailyQuote();
        expectedQuote.setId(id);

        when(dailyQuotesClient.getQuoteById(id)).thenReturn(expectedQuote);

        // Act
        Optional<DailyQuote> result = dailyQuoteService.getQuoteById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(dailyQuotesClient).getQuoteById(id);
    }

    @Test
    void getQuoteById_ShouldReturnEmptyWhenNotExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(dailyQuotesClient.getQuoteById(id)).thenReturn(null);

        // Act
        Optional<DailyQuote> result = dailyQuoteService.getQuoteById(id);

        // Assert
        assertTrue(result.isEmpty());
        verify(dailyQuotesClient).getQuoteById(id);
    }

    @Test
    void updateDailyQuote_ShouldReturnUpdatedQuote() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EditDailyQuotesRequest request = EditDailyQuotesRequest.builder()
                .quoteImage("updated-image-url")
                .userId(userId)
                .build();

        DailyQuote expectedQuote = DailyQuote.builder()
                .id(id)
                .quoteImage("updated-image-url")
                .userId(userId)
                .build();

        when(dailyQuotesClient.updateDailyQuote(eq(id), any(DailyQuote.class))).thenReturn(expectedQuote);

        // Act
        DailyQuote result = dailyQuoteService.updateDailyQuote(id, request);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("updated-image-url", result.getQuoteImage());
        assertEquals(userId, result.getUserId());
        verify(dailyQuotesClient).updateDailyQuote(eq(id), any(DailyQuote.class));
    }

    @Test
    void deleteDailyQuote_ShouldCallClientDelete() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(dailyQuotesClient).deleteDailyQuote(id);

        // Act
        dailyQuoteService.deleteDailyQuote(id);

        // Assert
        verify(dailyQuotesClient).deleteDailyQuote(id);
    }
}