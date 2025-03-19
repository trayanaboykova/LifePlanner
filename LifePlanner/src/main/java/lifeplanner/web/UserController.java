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
                                          @RequestParam(name="profilePictureFile", required=false) MultipartFile profilePictureFile) {

        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);
            return modelAndView;
        }

        // Handle profile picture upload
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            try {
                String uploadedUrl = cloudinaryService.uploadFile(profilePictureFile);
                userEditRequest.setProfilePicture(uploadedUrl); // Set the profilePicture URL
            } catch (Exception e) {
                bindingResult.rejectValue("profilePicture", "uploadError", "Could not upload image. Please try again.");
                return new ModelAndView("edit-profile");
            }
        }

        // Handle remove profile picture checkbox
        if (userEditRequest.isRemoveProfilePic()) {
            userEditRequest.setProfilePicture(null); // Set profilePicture to null if checkbox is checked
        }

        userService.editUserDetails(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("all-users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @PutMapping("/{id}/role") // PUT /users/{id}/role
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users/all-users";
    }

    @PutMapping("/{id}/status")
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/users/all-users";
    }
}