package lifeplanner.web.travel;

import lifeplanner.security.AuthenticationMetadata;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.service.TravelService;
import lifeplanner.user.model.User;
import lifeplanner.user.model.UserRole;
import lifeplanner.user.service.UserService;
import lifeplanner.validation.CustomAccessDeniedHandler;
import lifeplanner.web.dto.AddTripRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TravelController.class)
public class TravelControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TravelService travelService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final UUID tripId = UUID.randomUUID();
    private final User testUser = new User();
    private final Travel testTrip = new Travel();

    private AuthenticationMetadata getAuth() {
        return new AuthenticationMetadata(userId, "user", "password", UserRole.USER, true);
    }

    @Test
    void getTravelPage_ShouldReturnTravelView() throws Exception {
        testUser.setId(userId);
        testTrip.setId(tripId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(travelService.getTripsByUser(testUser)).thenReturn(Arrays.asList(testTrip));

        mockMvc.perform(get("/trips/travel").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("travel"))
                .andExpect(model().attribute("pageTitle", "Travel Plans"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("trips", Arrays.asList(testTrip)));
    }

    @Test
    void getAllTripsPage_ShouldReturnAllTripsView() throws Exception {
        testUser.setId(userId);
        testTrip.setId(tripId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(travelService.getTripsByUser(testUser)).thenReturn(Arrays.asList(testTrip));

        mockMvc.perform(get("/trips/all-trips").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-trips"))
                .andExpect(model().attribute("pageTitle", "All Trips"))
                .andExpect(model().attribute("trips", Arrays.asList(testTrip)));
    }

    @Test
    void getPastTripsPage_ShouldReturnPastTripsView() throws Exception {
        testUser.setId(userId);
        testTrip.setId(tripId);
        when(userService.getById(userId)).thenReturn(testUser);
        when(travelService.getTripsByUser(testUser)).thenReturn(Arrays.asList(testTrip));

        mockMvc.perform(get("/trips/past-trips").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("past-trips"))
                .andExpect(model().attribute("pageTitle", "Past Trips"))
                .andExpect(model().attribute("trips", Arrays.asList(testTrip)));
    }

    @Test
    void showAddTravelRequest_ShouldReturnAddTripView() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/trips/new").with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-trip"))
                .andExpect(model().attributeExists("pageTitle", "user", "addTripRequest"));
    }

    @Test
    void addTrip_WithValidRequest_ShouldRedirect() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        // Provide all required parameters, including tripStatus and tripName.
        mockMvc.perform(post("/trips")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("tripStatus", "UPCOMING")
                        .param("tripName", "Test Trip")
                        .param("destination", "Paris")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-10")
                        // Optionally, add extra parameters if needed:
                        .param("tripType", "VACATION")
                        .param("accommodation", "Hotel")
                        .param("transportationType", "AIRPLANE")
                        .param("notes", "Some notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips/travel"));

        verify(travelService).addTrip(any(AddTripRequest.class), eq(testUser));
    }


    @Test
    void addTrip_WithInvalidRequest_ShouldReturnAddTripForm() throws Exception {
        testUser.setId(userId);
        when(userService.getById(userId)).thenReturn(testUser);

        // Omitting required parameter "destination" to trigger a binding error.
        mockMvc.perform(post("/trips")
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-10"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-trip"));
    }

    @Test
    void showEditTripRequest_ShouldReturnEditTripView() throws Exception {
        testTrip.setId(tripId);
        when(travelService.getTripById(tripId)).thenReturn(testTrip);

        mockMvc.perform(get("/trips/{id}/edit", tripId).with(user(getAuth())))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-trip"))
                .andExpect(model().attributeExists("pageTitle", "trip", "editTravelRequest"));
    }

    @Test
    void updateTrip_WithValidRequest_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/trips/{id}/edit", tripId)
                        .with(user(getAuth()))
                        .with(csrf())
                        // Provide all required parameters for a valid EditTripRequest.
                        .param("tripStatus", "UPCOMING")
                        .param("tripName", "Updated Trip Name")
                        .param("destination", "Updated Destination")
                        .param("startDate", "2025-02-01")
                        .param("endDate", "2025-02-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips/travel"));
    }

    @Test
    void updateTrip_WithBindingErrors_ShouldReturnEditTripView() throws Exception {
        testTrip.setId(tripId);
        when(travelService.getTripById(tripId)).thenReturn(testTrip);

        // Passing an empty destination to simulate a binding error.
        mockMvc.perform(post("/trips/{id}/edit", tripId)
                        .with(user(getAuth()))
                        .with(csrf())
                        .param("destination", "")
                        .param("startDate", "2025-02-01")
                        .param("endDate", "2025-02-10"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-trip"))
                .andExpect(model().attributeExists("trip", "editTravelRequest"));
    }

    @Test
    void shareTrip_ShouldRedirectToTravelPage() throws Exception {
        mockMvc.perform(post("/trips/{id}/share", tripId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips/travel"));
    }

    @Test
    void removeSharing_ShouldRedirectToMySharedPosts() throws Exception {
        mockMvc.perform(post("/trips/{id}/remove", tripId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-shared-posts"));
    }

    @Test
    void deleteTrip_ShouldRedirectToTravelPage() throws Exception {
        mockMvc.perform(delete("/trips/{id}", tripId)
                        .with(user(getAuth()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips/travel"));
    }
}