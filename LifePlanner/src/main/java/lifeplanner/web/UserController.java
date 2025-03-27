package lifeplanner.web;

import jakarta.validation.Valid;
import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.service.CloudinaryService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.UserEditRequest;
import lifeplanner.web.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public UserController(UserService userService, CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getProfileMenu(@PathVariable UUID id, Model model) {
        model.addAttribute("pageTitle", "Edit Profile");
        User user = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", DTOMapper.mapUserToUserEditRequest(user));

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id,
                                          @Valid UserEditRequest userEditRequest,
                                          BindingResult bindingResult,
                                          @RequestParam(name="profilePictureFile", required=false) MultipartFile profilePictureFile,
                                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);
            return modelAndView;
        }

        // Handle profile picture upload
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadFile(profilePictureFile);
            userEditRequest.setProfilePicture(uploadedUrl);
        }

        // Handle remove profile picture checkbox
        if (userEditRequest.isRemoveProfilePic()) {
            userEditRequest.setProfilePicture(null);
        }

        userService.editUserDetails(id, userEditRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers() {
        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("all-users");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @DeleteMapping("/{id}/deactivate")
    public ModelAndView deactivateUserProfile(@PathVariable UUID id,
                                              @AuthenticationPrincipal AuthenticationMetadata currentUser) {
        userService.deactivateUserProfile(id);
        return new ModelAndView("redirect:/logout");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public String switchUserRole(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.switchRole(id);
        redirectAttributes.addFlashAttribute("successMessage", "User role updated successfully!");
        return "redirect:/users/all-users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public String switchUserStatus(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.switchStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully!");
        return "redirect:/users/all-users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/users/all-users";
    }
}