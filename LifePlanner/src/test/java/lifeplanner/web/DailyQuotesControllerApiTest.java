package lifeplanner.web;

import lifeplanner.exception.DomainException;
import lifeplanner.quotes.client.dto.AddDailyQuoteRequest;
import lifeplanner.quotes.client.dto.DailyQuote;
import lifeplanner.quotes.client.dto.EditDailyQuotesRequest;
import lifeplanner.quotes.service.DailyQuoteService;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyQuotesControllerApiTest {

    @Mock
    private UserService userService;

    @Mock
    private DailyQuoteService dailyQuoteService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private AuthenticationMetadata authenticationMetadata;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DailyQuotesController dailyQuotesController;

    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void showDailyQuotesPage_ShouldReturnViewWithQuotes() {
        // Arrange
        when(authenticationMetadata.getUserId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        List<DailyQuote> quotes = Collections.singletonList(new DailyQuote());
        when(dailyQuoteService.getQuotesByUserId(userId)).thenReturn(quotes);

        // Act
        ModelAndView result = dailyQuotesController.showDailyQuotesPage(model, authenticationMetadata);

        // Assert
        assertEquals("daily-quotes", result.getViewName());
        verify(model).addAttribute("pageTitle", "Daily Quotes");
        verify(model).addAttribute("quotes", quotes);
    }

    @Test
    void showAddQuoteRequest_ShouldReturnAddQuoteView() {
        // Arrange
        when(authenticationMetadata.getUserId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        // Act
        ModelAndView result = dailyQuotesController.showAddQuoteRequest(model, authenticationMetadata);

        // Assert
        assertEquals("add-daily-quote", result.getViewName());
        verify(model).addAttribute("pageTitle", "Add Daily Quote");
        verify(model).addAttribute(eq("addDailyQuoteRequest"), any(AddDailyQuoteRequest.class));
    }

    @Test
    void addDailyQuote_WithValidRequest_ShouldRedirect() {
        // Arrange
        AddDailyQuoteRequest request = new AddDailyQuoteRequest();
        request.setQuoteImage("test-image");
        when(bindingResult.hasErrors()).thenReturn(false);

        when(authenticationMetadata.getUserId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        // Act
        String result = dailyQuotesController.addDailyQuote(request, bindingResult, model, authenticationMetadata);

        // Assert
        assertEquals("redirect:/daily-quotes", result);
        verify(dailyQuoteService).addDailyQuote(eq(request), eq(testUser));
    }

    @Test
    void addDailyQuote_WithInvalidRequest_ShouldReturnForm() {
        // Arrange
        AddDailyQuoteRequest request = new AddDailyQuoteRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = dailyQuotesController.addDailyQuote(request, bindingResult, model, authenticationMetadata);

        // Assert
        assertEquals("add-daily-quote", result);
        verify(model).addAttribute("pageTitle", "Add Daily Quote");
        verify(dailyQuoteService, never()).addDailyQuote(any(AddDailyQuoteRequest.class), any(User.class));
    }

    @Test
    void showEditQuoteForm_ShouldReturnEditView() {
        // Arrange
        UUID quoteId = UUID.randomUUID();
        DailyQuote existingQuote = DailyQuote.builder()
                .id(quoteId)
                .quoteImage("existing-image")
                .userId(userId)
                .build();

        when(dailyQuoteService.getQuoteById(quoteId)).thenReturn(Optional.of(existingQuote));

        // Act
        ModelAndView result = dailyQuotesController.showEditQuoteForm(quoteId, model);

        // Assert
        assertEquals("edit-daily-quote", result.getViewName());
        verify(model).addAttribute("pageTitle", "Edit Daily Quote");
        verify(model).addAttribute(eq("editDailyQuoteRequest"), any());
    }

    @Test
    void showEditQuoteForm_WithNonExistingQuote_ShouldThrowException() {
        // Arrange
        UUID quoteId = UUID.randomUUID();
        when(dailyQuoteService.getQuoteById(quoteId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DomainException.class, () -> dailyQuotesController.showEditQuoteForm(quoteId, model));
    }


    @Test
    void updateDailyQuote_WithInvalidRequest_ShouldReturnForm() {
        // Arrange
        UUID quoteId = UUID.randomUUID();
        EditDailyQuotesRequest request = new EditDailyQuotesRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = dailyQuotesController.updateDailyQuote(quoteId, request, bindingResult, model);

        // Assert
        assertEquals("edit-daily-quote", result);
        verify(model).addAttribute("pageTitle", "Edit Daily Quote");
        verify(dailyQuoteService, never()).updateDailyQuote(any(UUID.class), any(EditDailyQuotesRequest.class));
    }

    @Test
    void updateDailyQuote_WithValidRequest_ShouldRedirect() {
        // Arrange
        UUID quoteId = UUID.randomUUID();
        EditDailyQuotesRequest request = new EditDailyQuotesRequest();
        request.setQuoteImage("updated-image");
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = dailyQuotesController.updateDailyQuote(quoteId, request, bindingResult, model);

        // Assert
        assertEquals("redirect:/daily-quotes", result);
        verify(dailyQuoteService).updateDailyQuote(quoteId, request);
        // Verify pageTitle is still added (matches actual controller behavior)
        verify(model).addAttribute("pageTitle", "Edit Daily Quote");
    }

    @Test
    void addDailyQuote_WithBindingErrors_ShouldReturnFormWithErrors() {
        // Arrange
        AddDailyQuoteRequest request = new AddDailyQuoteRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = dailyQuotesController.addDailyQuote(request, bindingResult, model, authenticationMetadata);

        // Assert
        assertEquals("add-daily-quote", result);
        verify(model).addAttribute("pageTitle", "Add Daily Quote");
        verify(dailyQuoteService, never()).addDailyQuote(any(), any());
    }

    @Test
    void deleteDailyQuote_ShouldRedirect() {
        // Arrange
        UUID quoteId = UUID.randomUUID();

        // Act
        String result = dailyQuotesController.deleteDailyQuote(quoteId);

        // Assert
        assertEquals("redirect:/daily-quotes", result);
        verify(dailyQuoteService, times(1)).deleteDailyQuote(quoteId);
    }
}
