package lifeplanner.travel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.trips.*;
import lifeplanner.travel.model.TransportationType;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.model.TripStatus;
import lifeplanner.travel.model.TripType;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.travel.service.TravelService;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.travel.service.TripLikesService;
import lifeplanner.web.dto.AddTripRequest;
import lifeplanner.web.dto.EditTripRequest;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TravelServiceUTest {

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private TripLikesService tripLikesService;

    @Mock
    private TripFavoriteService tripFavoriteService;

    @InjectMocks
    private TravelService travelService;

    @Test
    void testGetTripsByUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        List<Travel> trips = List.of(new Travel(), new Travel());
        when(travelRepository.findAllByOwner(user)).thenReturn(trips);

        List<Travel> result = travelService.getTripsByUser(user);
        assertEquals(2, result.size());
        verify(travelRepository).findAllByOwner(user);
    }

    @Test
    void testAddTrip() {
        User user = new User();
        user.setId(UUID.randomUUID());
        AddTripRequest request = new AddTripRequest();
        request.setTripStatus(TripStatus.UPCOMING);
        request.setTripName("Trip to Paris");
        request.setDestination("Paris");
        request.setStartDate(LocalDate.of(2025, 6, 1));
        request.setEndDate(LocalDate.of(2025, 6, 10));
        request.setTripType(TripType.VACATION);
        request.setAccommodation("Hotel");
        request.setTransportationType(TransportationType.AIRPLANE);
        request.setNotes("Some notes");

        travelService.addTrip(request, user);

        ArgumentCaptor<Travel> travelCaptor = ArgumentCaptor.forClass(Travel.class);
        verify(travelRepository).save(travelCaptor.capture());
        Travel savedTravel = travelCaptor.getValue();

        assertEquals("Trip to Paris", savedTravel.getTripName());
        assertEquals("Paris", savedTravel.getDestination());
        assertEquals(LocalDate.of(2025, 6, 1), savedTravel.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 10), savedTravel.getEndDate());
        assertEquals("Hotel", savedTravel.getAccommodation());
        assertFalse(savedTravel.isVisible());
        assertEquals(ApprovalStatus.PENDING, savedTravel.getApprovalStatus());
        assertEquals(user, savedTravel.getOwner());
    }

    @Test
    void testGetTripByIdNotFound() {
        UUID tripId = UUID.randomUUID();
        when(travelRepository.findById(tripId)).thenReturn(Optional.empty());
        assertThrows(TripNotFoundException.class, () -> travelService.getTripById(tripId));
    }

    @Test
    void testGetTripByIdFound() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));
        Travel result = travelService.getTripById(tripId);
        assertEquals(tripId, result.getId());
    }

    @Test
    void testEditTrip() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setTripName("Old Name");
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));

        EditTripRequest editRequest = EditTripRequest.builder().build();
        editRequest.setTripName("New Name");
        editRequest.setDestination("London");
        editRequest.setStartDate(LocalDate.of(2025, 7, 1));
        editRequest.setEndDate(LocalDate.of(2025, 7, 15));
        editRequest.setTripStatus(TripStatus.FINISHED);
        editRequest.setTripType(TripType.BUSINESS);
        editRequest.setAccommodation("Apartment");
        editRequest.setTransportationType(TransportationType.TRAIN);
        editRequest.setNotes("Updated notes");

        travelService.editTrip(tripId, editRequest);
        ArgumentCaptor<Travel> captor = ArgumentCaptor.forClass(Travel.class);
        verify(travelRepository).save(captor.capture());
        Travel updatedTrip = captor.getValue();

        assertEquals("New Name", updatedTrip.getTripName());
        assertEquals("London", updatedTrip.getDestination());
        assertEquals(LocalDate.of(2025, 7, 1), updatedTrip.getStartDate());
        assertEquals(LocalDate.of(2025, 7, 15), updatedTrip.getEndDate());
        assertEquals(TripStatus.FINISHED, updatedTrip.getTripStatus());
        assertEquals(TripType.BUSINESS, updatedTrip.getTripType());
        assertEquals("Apartment", updatedTrip.getAccommodation());
        assertEquals(TransportationType.TRAIN, updatedTrip.getTransportation());
        assertEquals("Updated notes", updatedTrip.getNotes());
    }

    @Test
    void testShareTripNotFound() {
        UUID tripId = UUID.randomUUID();
        when(travelRepository.findById(tripId)).thenReturn(Optional.empty());
        assertThrows(TripNotFoundException.class, () -> travelService.shareTrip(tripId));
    }

    @Test
    void testShareTripAlreadyShared() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setVisible(true);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));
        assertThrows(TripAlreadySharedException.class, () -> travelService.shareTrip(tripId));
    }

    @Test
    void testShareTripRejected() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.REJECTED);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));
        assertThrows(TripRejectedException.class, () -> travelService.shareTrip(tripId));
    }

    @Test
    void testShareTripPending() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.PENDING);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));
        assertThrows(TripPendingApprovalException.class, () -> travelService.shareTrip(tripId));
    }

    @Test
    void testShareTripApproved() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.APPROVED);
        trip.setVisible(false);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));

        travelService.shareTrip(tripId);
        assertTrue(trip.isVisible());
        verify(travelRepository).save(trip);
    }

    @Test
    void testGetAllTrips() {
        List<Travel> trips = List.of(new Travel(), new Travel());
        when(travelRepository.findAll()).thenReturn(trips);
        List<Travel> result = travelService.getAllTrips();
        assertEquals(2, result.size());
    }

    @Test
    void testGetLikeCountsForTrips() {
        Travel trip1 = new Travel();
        UUID id1 = UUID.randomUUID();
        trip1.setId(id1);
        Travel trip2 = new Travel();
        UUID id2 = UUID.randomUUID();
        trip2.setId(id2);

        when(tripLikesService.getLikeCount(id1)).thenReturn(7L);
        when(tripLikesService.getLikeCount(id2)).thenReturn(3L);

        Map<UUID, Long> counts = travelService.getLikeCountsForTrips(List.of(trip1, trip2));
        assertEquals(7L, counts.get(id1));
        assertEquals(3L, counts.get(id2));
    }

    @Test
    void testGetFavoriteCountsForTrips() {
        Travel trip1 = new Travel();
        UUID id1 = UUID.randomUUID();
        trip1.setId(id1);
        Travel trip2 = new Travel();
        UUID id2 = UUID.randomUUID();
        trip2.setId(id2);

        when(tripFavoriteService.getFavoriteCount(id1)).thenReturn(4L);
        when(tripFavoriteService.getFavoriteCount(id2)).thenReturn(2L);

        Map<UUID, Long> counts = travelService.getFavoriteCountsForTrips(List.of(trip1, trip2));
        assertEquals(4L, counts.get(id1));
        assertEquals(2L, counts.get(id2));
    }

    @Test
    void testGetApprovedSharedTrips() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Travel trip1 = new Travel();
        trip1.setId(UUID.randomUUID());
        User owner1 = new User();
        owner1.setId(UUID.randomUUID());
        trip1.setOwner(owner1);
        trip1.setApprovalStatus(ApprovalStatus.APPROVED);
        trip1.setVisible(true);

        // trip with same owner as currentUser, should be filtered out
        Travel trip2 = new Travel();
        trip2.setId(UUID.randomUUID());
        trip2.setOwner(currentUser);
        trip2.setApprovalStatus(ApprovalStatus.APPROVED);
        trip2.setVisible(true);

        when(travelRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED))
                .thenReturn(List.of(trip1, trip2));

        List<Travel> result = travelService.getApprovedSharedTrips(currentUser);
        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    void testGetMySharedTrips() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Travel trip1 = new Travel();
        trip1.setId(UUID.randomUUID());
        trip1.setOwner(currentUser);
        trip1.setVisible(true);

        // trip with different owner, should be filtered out
        Travel trip2 = new Travel();
        trip2.setId(UUID.randomUUID());
        User owner2 = new User();
        owner2.setId(UUID.randomUUID());
        trip2.setOwner(owner2);
        trip2.setVisible(true);

        when(travelRepository.findAllByVisibleTrue())
                .thenReturn(List.of(trip1, trip2));

        List<Travel> result = travelService.getMySharedTrips(currentUser);
        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    void testRemoveSharingNotFound() {
        UUID tripId = UUID.randomUUID();
        when(travelRepository.findById(tripId)).thenReturn(Optional.empty());
        assertThrows(TripNotFoundException.class, () -> travelService.removeSharing(tripId));
    }

    @Test
    void testRemoveSharing() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setVisible(true);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));

        travelService.removeSharing(tripId);
        assertFalse(trip.isVisible());
        verify(travelRepository).save(trip);
    }

    @Test
    void testDeleteTripById() {
        UUID tripId = UUID.randomUUID();
        travelService.deleteTripById(tripId);
        verify(travelRepository).deleteById(tripId);
    }

    @Test
    void testGetPendingTravel() {
        Travel trip1 = new Travel();
        trip1.setApprovalStatus(ApprovalStatus.PENDING);
        Travel trip2 = new Travel();
        trip2.setApprovalStatus(ApprovalStatus.PENDING);
        when(travelRepository.findAllByApprovalStatus(ApprovalStatus.PENDING))
                .thenReturn(List.of(trip1, trip2));

        List<Travel> result = travelService.getPendingTravel();
        assertEquals(2, result.size());
    }

    @Test
    void testApproveTripAlreadyApproved() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.APPROVED);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));
        assertThrows(TripAlreadyApprovedException.class, () -> travelService.approveTrip(tripId));
    }

    @Test
    void testApproveTrip() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.PENDING);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));

        travelService.approveTrip(tripId);
        assertEquals(ApprovalStatus.APPROVED, trip.getApprovalStatus());
        verify(travelRepository).save(trip);
    }

    @Test
    void testRejectTripAlreadyRejected() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.REJECTED);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));
        assertThrows(TripAlreadyRejectedException.class, () -> travelService.rejectTrip(tripId));
    }

    @Test
    void testRejectTrip() {
        UUID tripId = UUID.randomUUID();
        Travel trip = new Travel();
        trip.setId(tripId);
        trip.setApprovalStatus(ApprovalStatus.PENDING);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(trip));

        travelService.rejectTrip(tripId);
        assertEquals(ApprovalStatus.REJECTED, trip.getApprovalStatus());
        verify(travelRepository).save(trip);
    }
}