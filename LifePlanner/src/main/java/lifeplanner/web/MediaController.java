package lifeplanner.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.media.model.Media;
import lifeplanner.media.service.MediaService;
import lifeplanner.user.model.User;
import lifeplanner.user.service.UserService;
import lifeplanner.web.dto.AddMediaRequest;
import lifeplanner.web.dto.EditBookRequest;
import lifeplanner.web.dto.EditMediaRequest;
import lifeplanner.web.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;
    private final UserService userService;

    @Autowired
    public MediaController(MediaService mediaService, UserService userService) {
        this.mediaService = mediaService;
        this.userService = userService;
    }

    @GetMapping("/all-media")
    public String getAllMediaPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "All Media");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Media> userMedia = mediaService.getMediaByUser(user);

        model.addAttribute("media", userMedia);

        return "all-media";
    }

    @GetMapping("/watched")
    public String getWatchedPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Watched");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Media> userMedia = mediaService.getMediaByUser(user);

        model.addAttribute("media", userMedia);

        return "watched";
    }

    @GetMapping("/watchlist")
    public String getWatchlistPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Watchlist");

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        List<Media> userMedia = mediaService.getMediaByUser(user);

        model.addAttribute("media", userMedia);

        return "watchlist";
    }

    @GetMapping("/new")
    public ModelAndView showAddMediaRequest(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Add Media");

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-media");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addMediaRequest", new AddMediaRequest());

        return modelAndView;
    }

    @PostMapping
    public String addMedia(@Valid AddMediaRequest addMediaRequest,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model) {

        model.addAttribute("pageTitle", "Add Media");

        if (bindingResult.hasErrors()) {
            return "add-media";
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        mediaService.addMedia(addMediaRequest, user);

        return "redirect:/media/all-media";
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditMediaRequest(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("pageTitle", "Edit Media");

        Media media = mediaService.getMediaById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-media");
        modelAndView.addObject("media", media);
        modelAndView.addObject("editMediaRequest", DTOMapper.mapMediaToEditMediaRequest(media));
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView updateBook(@PathVariable("id") UUID id,
                                   @Valid @ModelAttribute("editMediaRequest") EditMediaRequest editMediaRequest,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Media media = mediaService.getMediaById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-media");
            modelAndView.addObject("media", media);
            modelAndView.addObject("editMediaRequest", editMediaRequest);
            return modelAndView;
        }

        mediaService.editMedia(id, editMediaRequest);
        return new ModelAndView("redirect:/media/all-media");
    }

    @PostMapping("/{id}/share")
    public String shareMedia(@PathVariable UUID id) {

        mediaService.shareMedia(id);

        return "redirect:/media/all-media";
    }

    @DeleteMapping("/{id}")
    public String deleteMedia(@PathVariable UUID id) {

        mediaService.deleteMediaById(id);

        return "redirect:/media/all-media";
    }
}