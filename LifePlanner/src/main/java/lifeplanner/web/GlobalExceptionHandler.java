package lifeplanner.web;

import jakarta.servlet.http.HttpServletRequest;
import lifeplanner.exception.*;
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

        // Redirect back to the profile edit page
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
}