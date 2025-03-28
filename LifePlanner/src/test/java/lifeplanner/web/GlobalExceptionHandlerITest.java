package lifeplanner.web;

import lifeplanner.exception.DomainException;
import lifeplanner.exception.books.*;
import lifeplanner.exception.goals.*;
import lifeplanner.exception.media.*;
import lifeplanner.exception.recipes.*;
import lifeplanner.exception.trips.*;
import lifeplanner.exception.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GlobalExceptionHandlerITest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ----- USERS -----
    @Test
    public void testHandleRegistrationErrorsUsername() {
        UsernameAlreadyExistsException ex = new UsernameAlreadyExistsException("Test username already exists");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRegistrationErrors(redirectAttributes, ex);
        assertEquals("redirect:/register", view);
        assertEquals("Test username already exists",
                redirectAttributes.getFlashAttributes().get("usernameAlreadyExistMessage"));
    }

    @Test
    public void testHandleRegistrationErrorsEmail() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Test email already exists");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRegistrationErrors(redirectAttributes, ex);
        assertEquals("redirect:/register", view);
        assertEquals("Test email already exists",
                redirectAttributes.getFlashAttributes().get("emailAlreadyExistMessage"));
    }

    @Test
    public void testHandlePasswordChangeException() {
        PasswordChangeException ex = new PasswordChangeException("Test password change error");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handlePasswordChangeException(redirectAttributes, ex);
        assertEquals("redirect:/profile/edit", view);
        assertEquals("Test password change error",
                redirectAttributes.getFlashAttributes().get("passwordChangeError"));
    }

    // ----- FILE UPLOAD -----
    @Test
    public void testHandleFileUploadErrors_MaxUploadSize_WithReferer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost/previous");
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1000);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleFileUploadErrors(redirectAttributes, request, ex);
        assertEquals("redirect:http://localhost/previous", view);
        assertEquals("File size exceeds the maximum allowed limit",
                redirectAttributes.getFlashAttributes().get("uploadError"));
    }

    @Test
    public void testHandleFileUploadErrors_Cloudinary() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // No Referer header set → should default to "/home"
        CloudinaryUploadException ex = new CloudinaryUploadException("Cloudinary error");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleFileUploadErrors(redirectAttributes, request, ex);
        assertEquals("redirect:/home", view);
        assertEquals("Cloudinary error",
                redirectAttributes.getFlashAttributes().get("uploadError"));
    }

    // ----- SESSION -----
    @Test
    public void testHandleSessionExpired() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleSessionExpired(redirectAttributes);
        assertEquals("redirect:/login", view);
        assertEquals(true, redirectAttributes.getFlashAttributes().get("sessionExpired"));
    }

    // ----- ADMIN -----
    @Test
    public void testHandleAdminDeletion() {
        String msg = "Cannot delete admin";
        AdminDeletionException ex = new AdminDeletionException(msg);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleAdminDeletion(redirectAttributes, ex);
        assertEquals("redirect:/users/all-users", view);
        assertEquals(msg, redirectAttributes.getFlashAttributes().get("adminError"));
    }

    // ----- NOT FOUND / BAD REQUEST -----
    @Test
    public void testHandleNotFoundExceptions() {
        ExtendedModelMap model = new ExtendedModelMap();
        ModelAndView mav = handler.handleNotFoundExceptions(model);
        assertEquals("not-found", mav.getViewName());
        assertEquals("Page Not Found", model.get("pageTitle"));
    }

    // ----- ACCESS DENIED -----
    @Test
    public void testHandleAccessDenied() {
        ExtendedModelMap model = new ExtendedModelMap();
        ModelAndView mav = handler.handleAccessDenied(model);
        assertEquals("access-denied", mav.getViewName());
        assertEquals("Access Denied", model.get("pageTitle"));
    }

    // ----- INTERNAL SERVER ERROR -----
    @Test
    public void testHandleAnyException() {
        ExtendedModelMap model = new ExtendedModelMap();
        Exception ex = new Exception("Internal error");
        ModelAndView mav = handler.handleAnyException(ex, model);
        assertEquals("server-error", mav.getViewName());
        assertEquals("Server Error", model.get("pageTitle"));
        assertEquals("An unexpected error occurred. Please try again later.",
                mav.getModel().get("errorMessage"));
    }

    // ----- DOMAIN -----
    @Test
    public void testHandleDomainException() {
        DomainException ex = new DomainException("Domain error");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleDomainException(redirectAttributes, ex);
        assertEquals("redirect:/home", view);
        assertEquals("Domain error", redirectAttributes.getFlashAttributes().get("error"));
    }

    // ----- BOOKS -----
    @Test
    public void testHandleBookNotFound() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b001");
        BookNotFoundException ex = new BookNotFoundException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleBookNotFound(ex, redirectAttributes);
        assertEquals("redirect:/books/my-books", view);
        String expected = "Book not found: " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleBookAlreadyShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b002");
        BookAlreadySharedException ex = new BookAlreadySharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleAlreadyShared(ex, redirectAttributes);
        assertEquals("redirect:/books/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleBookNotShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b003");
        BookNotSharedException ex = new BookNotSharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleNotShared(ex, redirectAttributes);
        assertEquals("redirect:/books/" + id, view);
        assertEquals("This book isn't shared yet. Use the Share button to proceed.",
                redirectAttributes.getFlashAttributes().get("warning"));
    }

    @Test
    public void testHandleBookRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b004");
        BookRejectedException ex = new BookRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRejectedBook(ex, redirectAttributes);
        assertEquals("redirect:/books/" + id + "/edit", view);
        String expected = "Cannot share rejected book. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleBookPendingApproval() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b005");
        BookPendingApprovalException ex = new BookPendingApprovalException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handlePendingApproval(ex, redirectAttributes);
        assertEquals("redirect:/books/my-books", view);
        String expected = "Book pending approval. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("info"));
    }

    @Test
    public void testHandleBookAlreadyApproved() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b006");
        BookAlreadyApprovedException ex = new BookAlreadyApprovedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleAlreadyApproved(ex, redirectAttributes);
        assertEquals("redirect:/books/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("inlineError"));
    }

    @Test
    public void testHandleBookAlreadyRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000b007");
        BookAlreadyRejectedException ex = new BookAlreadyRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleAlreadyRejected(ex, redirectAttributes);
        assertEquals("redirect:/books/" + id + "/edit", view);
        String expected = ex.getMessage() + " Please edit the book and resubmit for approval.";
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("inlineWarning"));
    }

    // ----- MEDIA -----
    @Test
    public void testHandleMediaNotFound() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c001");
        MediaNotFoundException ex = new MediaNotFoundException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleMediaNotFound(ex, redirectAttributes);
        assertEquals("redirect:/media/all-media", view);
        String expected = "Media not found: " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleMediaAlreadyShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c002");
        MediaAlreadySharedException ex = new MediaAlreadySharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleMediaAlreadyShared(ex, redirectAttributes);
        assertEquals("redirect:/media/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleMediaNotShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c003");
        MediaNotSharedException ex = new MediaNotSharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleMediaNotShared(ex, redirectAttributes);
        assertEquals("redirect:/media/" + id, view);
        assertEquals("This media isn't shared yet. Use the Share button to proceed.",
                redirectAttributes.getFlashAttributes().get("warning"));
    }

    @Test
    public void testHandleMediaRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c004");
        MediaRejectedException ex = new MediaRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRejectedMedia(ex, redirectAttributes);
        assertEquals("redirect:/media/" + id + "/edit", view);
        String expected = "Cannot share rejected media. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleMediaPendingApproval() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c005");
        MediaPendingApprovalException ex = new MediaPendingApprovalException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleMediaPendingApproval(ex, redirectAttributes);
        assertEquals("redirect:/media/my-media", view);
        String expected = "Media pending approval. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("info"));
    }

    @Test
    public void testHandleMediaAlreadyApproved() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c006");
        MediaAlreadyApprovedException ex = new MediaAlreadyApprovedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleMediaAlreadyApproved(ex, redirectAttributes);
        assertEquals("redirect:/media/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("inlineError"));
    }

    @Test
    public void testHandleMediaAlreadyRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000c007");
        MediaAlreadyRejectedException ex = new MediaAlreadyRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleMediaAlreadyRejected(ex, redirectAttributes);
        assertEquals("redirect:/media/" + id + "/edit", view);
        String expected = ex.getMessage() + " Please edit the media and resubmit for approval.";
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("inlineWarning"));
    }

    // ----- RECIPES -----
    @Test
    public void testHandleRecipeNotFound() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d001");
        RecipeNotFoundException ex = new RecipeNotFoundException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRecipeNotFound(ex, redirectAttributes, null);
        assertEquals("redirect:/recipes/all-recipes", view);
        String expected = "Recipe not found: " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleRecipeAlreadyShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d002");
        RecipeAlreadySharedException ex = new RecipeAlreadySharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRecipeAlreadyShared(ex, redirectAttributes);
        assertEquals("redirect:/recipes/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleRecipeNotShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d003");
        RecipeNotSharedException ex = new RecipeNotSharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRecipeNotShared(ex, redirectAttributes);
        assertEquals("redirect:/recipes/" + id, view);
        assertEquals("This recipe isn't shared yet. Use the Share button to proceed.",
                redirectAttributes.getFlashAttributes().get("warning"));
    }

    @Test
    public void testHandleRecipeRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d004");
        RecipeRejectedException ex = new RecipeRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRejectedRecipe(ex, redirectAttributes);
        assertEquals("redirect:/recipes/" + id + "/edit", view);
        String expected = "Cannot share rejected recipe. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleRecipePendingApproval() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d005");
        RecipePendingApprovalException ex = new RecipePendingApprovalException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRecipePendingApproval(ex, redirectAttributes);
        assertEquals("redirect:/recipes/all-recipes", view);
        String expected = "Recipe pending approval. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("info"));
    }

    @Test
    public void testHandleRecipeAlreadyApproved() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d006");
        RecipeAlreadyApprovedException ex = new RecipeAlreadyApprovedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRecipeAlreadyApproved(ex, redirectAttributes);
        assertEquals("redirect:/recipes/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("inlineError"));
    }

    @Test
    public void testHandleRecipeAlreadyRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000d007");
        RecipeAlreadyRejectedException ex = new RecipeAlreadyRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRecipeAlreadyRejected(ex, redirectAttributes);
        assertEquals("redirect:/recipes/" + id + "/edit", view);
        String expected = ex.getMessage() + " Please edit the recipe and resubmit for approval.";
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("inlineWarning"));
    }

    @Test
    public void testHandleInvalidIngredient() {
        InvalidIngredientException ex = new InvalidIngredientException("Invalid ingredient");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleInvalidIngredient(ex, redirectAttributes);
        assertEquals("redirect:/recipes/new", view);
        assertEquals("Invalid ingredient", redirectAttributes.getFlashAttributes().get("error"));
    }

    // ----- TRIPS -----
    @Test
    public void testHandleTripNotFound_WithReferer() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e001");
        TripNotFoundException ex = new TripNotFoundException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost/previousTrip");
        String view = handler.handleTripNotFound(ex, redirectAttributes, request);
        assertEquals("redirect:http://localhost/previousTrip", view);
        String expected = "Trip not found: " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleTripNotFound_WithoutReferer() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e001");
        TripNotFoundException ex = new TripNotFoundException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        MockHttpServletRequest request = new MockHttpServletRequest();
        String view = handler.handleTripNotFound(ex, redirectAttributes, request);
        assertEquals("redirect:/trips/travel", view);
        String expected = "Trip not found: " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleTripAlreadyShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e002");
        TripAlreadySharedException ex = new TripAlreadySharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleTripAlreadyShared(ex, redirectAttributes);
        assertEquals("redirect:/trips/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleTripNotShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e003");
        TripNotSharedException ex = new TripNotSharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleTripNotShared(ex, redirectAttributes);
        assertEquals("redirect:/trips/" + id, view);
        assertEquals("This trip isn't shared yet. Use the Share button to proceed.",
                redirectAttributes.getFlashAttributes().get("warning"));
    }

    @Test
    public void testHandleTripRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e004");
        TripRejectedException ex = new TripRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRejectedTrip(ex, redirectAttributes);
        assertEquals("redirect:/trips/" + id + "/edit", view);
        String expected = "Cannot share rejected trip. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleTripPendingApproval() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e005");
        TripPendingApprovalException ex = new TripPendingApprovalException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleTripPendingApproval(ex, redirectAttributes);
        assertEquals("redirect:/trips/travel", view);
        String expected = "Trip pending approval. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("info"));
    }

    @Test
    public void testHandleTripAlreadyApproved() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e006");
        TripAlreadyApprovedException ex = new TripAlreadyApprovedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleTripAlreadyApproved(ex, redirectAttributes);
        assertEquals("redirect:/trips/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("inlineError"));
    }

    @Test
    public void testHandleTripAlreadyRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000e007");
        TripAlreadyRejectedException ex = new TripAlreadyRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleTripAlreadyRejected(ex, redirectAttributes);
        assertEquals("redirect:/trips/" + id + "/edit", view);
        String expected = ex.getMessage() + " Please edit the trip and resubmit for approval.";
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("inlineWarning"));
    }

    @Test
    public void testHandleInvalidTripDates() {
        InvalidTripDatesException ex = new InvalidTripDatesException("Invalid dates");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleInvalidTripDates(ex, redirectAttributes);
        assertEquals("redirect:/trips/new", view);
        assertEquals("Invalid dates", redirectAttributes.getFlashAttributes().get("error"));
    }

    // ----- GOALS -----
    @Test
    public void testHandleGoalNotFound() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f001");
        GoalNotFoundException ex = new GoalNotFoundException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleGoalNotFound(ex, redirectAttributes);
        assertEquals("redirect:/goals/my-goals", view);
        String expected = "Goal not found: " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleGoalAlreadyShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f002");
        GoalAlreadySharedException ex = new GoalAlreadySharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleGoalAlreadyShared(ex, redirectAttributes);
        assertEquals("redirect:/goals/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleGoalNotShared() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f003");
        GoalNotSharedException ex = new GoalNotSharedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleGoalNotShared(ex, redirectAttributes);
        assertEquals("redirect:/goals/" + id, view);
        assertEquals("This goal isn't shared yet. Use the Share button to proceed.",
                redirectAttributes.getFlashAttributes().get("warning"));
    }

    @Test
    public void testHandleGoalRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f004");
        GoalRejectedException ex = new GoalRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleRejectedGoal(ex, redirectAttributes);
        assertEquals("redirect:/goals/" + id + "/edit", view);
        String expected = "Cannot share rejected goal. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    public void testHandleGoalPendingApproval() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f005");
        GoalPendingApprovalException ex = new GoalPendingApprovalException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleGoalPendingApproval(ex, redirectAttributes);
        assertEquals("redirect:/goals/my-goals", view);
        String expected = "Goal pending approval. " + ex.getMessage();
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("info"));
    }

    @Test
    public void testHandleGoalAlreadyApproved() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f006");
        GoalAlreadyApprovedException ex = new GoalAlreadyApprovedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleGoalAlreadyApproved(ex, redirectAttributes);
        assertEquals("redirect:/goals/" + id, view);
        assertEquals(ex.getMessage(), redirectAttributes.getFlashAttributes().get("inlineError"));
    }

    @Test
    public void testHandleGoalAlreadyRejected() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000f007");
        GoalAlreadyRejectedException ex = new GoalAlreadyRejectedException(id);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleGoalAlreadyRejected(ex, redirectAttributes);
        assertEquals("redirect:/goals/" + id + "/edit", view);
        String expected = ex.getMessage() + " Please edit the goal and resubmit for approval.";
        assertEquals(expected, redirectAttributes.getFlashAttributes().get("inlineWarning"));
    }

    @Test
    public void testHandleInvalidGoalDates() {
        InvalidGoalDatesException ex = new InvalidGoalDatesException("Invalid goal dates");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = handler.handleInvalidGoalDates(ex, redirectAttributes);
        assertEquals("redirect:/goals/new", view);
        assertEquals("Invalid goal dates", redirectAttributes.getFlashAttributes().get("error"));
    }
}