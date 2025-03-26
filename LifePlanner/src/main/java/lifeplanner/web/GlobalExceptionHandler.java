package lifeplanner.web;

import jakarta.servlet.http.HttpServletRequest;
import lifeplanner.exception.DomainException;
import lifeplanner.exception.books.*;
import lifeplanner.exception.media.*;
import lifeplanner.exception.user.AdminDeletionException;
import lifeplanner.exception.user.CloudinaryUploadException;
import lifeplanner.exception.user.EmailAlreadyExistsException;
import lifeplanner.exception.user.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    // USERS
    @ExceptionHandler({UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class})
    public String handleRegistrationErrors(RedirectAttributes redirectAttributes,
                                           RuntimeException exception) {
        String attributeName = exception instanceof UsernameAlreadyExistsException
                ? "usernameAlreadyExistMessage"
                : "emailAlreadyExistMessage";

        redirectAttributes.addFlashAttribute(attributeName, exception.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler({CloudinaryUploadException.class, MaxUploadSizeExceededException.class})
    public String handleFileUploadErrors(RedirectAttributes redirectAttributes,
                                         HttpServletRequest request,
                                         Exception exception) {
        String errorMessage = exception instanceof MaxUploadSizeExceededException
                ? "File size exceeds the maximum allowed limit"
                : exception.getMessage();

        redirectAttributes.addFlashAttribute("uploadError", errorMessage);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }

    @ExceptionHandler(SessionAuthenticationException.class)
    public String handleSessionExpired(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("sessionExpired", true);
        return "redirect:/login";
    }

    @ExceptionHandler(AdminDeletionException.class)
    public String handleAdminDeletion(RedirectAttributes redirectAttributes,
                                      AdminDeletionException exception) {
        redirectAttributes.addFlashAttribute("adminError", exception.getMessage());
        return "redirect:/users/all-users";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundExceptions(Model model) {
        model.addAttribute("pageTitle", "Page Not Found");
        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(Model model) {
        model.addAttribute("pageTitle", "Access Denied");
        return new ModelAndView("access-denied");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception, Model model) {
        model.addAttribute("pageTitle", "Server Error");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("server-error");
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        return modelAndView;
    }

    // BOOKS
    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFound(BookNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Book not found: " + ex.getMessage());
        return "redirect:/books/my-books";
    }

    @ExceptionHandler(BookAlreadySharedException.class)
    public String handleAlreadyShared(BookAlreadySharedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/books/" + ex.getBookId();
    }

    @ExceptionHandler(BookNotSharedException.class)
    public String handleNotShared(BookNotSharedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warning", "This book isn't shared yet. Use the Share button to proceed.");
        return "redirect:/books/" + ex.getBookId();
    }

    @ExceptionHandler(BookRejectedException.class)
    public String handleRejectedBook(BookRejectedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "Cannot share rejected book. " + ex.getMessage());
        return "redirect:/books/" + ex.getBookId() + "/edit";
    }

    @ExceptionHandler(BookPendingApprovalException.class)
    public String handlePendingApproval(BookPendingApprovalException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info",
                "Book pending approval. " + ex.getMessage());
        return "redirect:/books/my-books";
    }

    @ExceptionHandler(BookAlreadyApprovedException.class)
    public String handleAlreadyApproved(BookAlreadyApprovedException ex,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("inlineError", ex.getMessage());
        return "redirect:/books/" + ex.getBookId(); // Stay on current book page
    }

    @ExceptionHandler(BookAlreadyRejectedException.class)
    public String handleAlreadyRejected(BookAlreadyRejectedException ex,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("inlineWarning",
                ex.getMessage() + " Please edit the book and resubmit for approval.");
        return "redirect:/books/" + ex.getBookId() + "/edit"; // Go directly to edit page
    }

    // MEDIA
    @ExceptionHandler(MediaNotFoundException.class)
    public String handleMediaNotFound(MediaNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Media not found: " + ex.getMessage());
        return "redirect:/media/all-media";
    }

    @ExceptionHandler(MediaAlreadySharedException.class)
    public String handleMediaAlreadyShared(MediaAlreadySharedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/media/" + ex.getMediaId();
    }

    @ExceptionHandler(MediaNotSharedException.class)
    public String handleMediaNotShared(MediaNotSharedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warning", "This media isn't shared yet. Use the Share button to proceed.");
        return "redirect:/media/" + ex.getMediaId();
    }

    @ExceptionHandler(MediaRejectedException.class)
    public String handleRejectedMedia(MediaRejectedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "Cannot share rejected media. " + ex.getMessage());
        return "redirect:/media/" + ex.getMediaId() + "/edit";
    }

    @ExceptionHandler(MediaPendingApprovalException.class)
    public String handleMediaPendingApproval(MediaPendingApprovalException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info",
                "Media pending approval. " + ex.getMessage());
        return "redirect:/media/my-media";
    }

    @ExceptionHandler(MediaAlreadyApprovedException.class)
    public String handleMediaAlreadyApproved(MediaAlreadyApprovedException ex,
                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("inlineError", ex.getMessage());
        return "redirect:/media/" + ex.getMediaId(); // Stay on current media page
    }

    @ExceptionHandler(MediaAlreadyRejectedException.class)
    public String handleMediaAlreadyRejected(MediaAlreadyRejectedException ex,
                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("inlineWarning",
                ex.getMessage() + " Please edit the media and resubmit for approval.");
        return "redirect:/media/" + ex.getMediaId() + "/edit"; // Go directly to edit page
    }

    // RECIPES

    // TRAVEL

    // GOALS

}