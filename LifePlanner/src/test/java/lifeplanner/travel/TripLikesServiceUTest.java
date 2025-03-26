package lifeplanner.travel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.DomainException;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.model.TripLikes;
import lifeplanner.travel.model.TripLikesId;
import lifeplanner.travel.repository.TripLikesRepository;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.travel.service.TripLikesService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TripLikesServiceUTest {

    @Mock
    private TripLikesRepository tripLikesRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripLikesService tripLikesService;

    @Test
    void testGetLikeCount() {
        UUID tripId = UUID.randomUUID();
        when(tripLikesRepository.countByTripId(tripId)).thenReturn(8L);
        long count = tripLikesService.getLikeCount(tripId);
        assertEquals(8L, count);
    }

    @Test
    void testToggleLikeRemove() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripLikesId likeId = new TripLikesId(tripId, userId);

        when(tripLikesRepository.existsById(likeId)).thenReturn(true);

        boolean result = tripLikesService.toggleLike(tripId, userId);
        assertFalse(result);
        verify(tripLikesRepository).deleteById(likeId);
    }

    @Test
    void testToggleLikeAdd() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripLikesId likeId = new TripLikesId(tripId, userId);

        when(tripLikesRepository.existsById(likeId)).thenReturn(false);

        Travel travel = new Travel();
        travel.setId(tripId);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(travel));

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = tripLikesService.toggleLike(tripId, userId);
        assertTrue(result);
        ArgumentCaptor<TripLikes> captor = ArgumentCaptor.forClass(TripLikes.class);
        verify(tripLikesRepository).save(captor.capture());
        TripLikes savedLike = captor.getValue();
        assertEquals(likeId, savedLike.getId());
        assertEquals(travel, savedLike.getTrip());
        assertEquals(user, savedLike.getUser());
    }

    @Test
    void testToggleLikeTripNotFound() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripLikesId likeId = new TripLikesId(tripId, userId);

        when(tripLikesRepository.existsById(likeId)).thenReturn(false);
        when(travelRepository.findById(tripId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> tripLikesService.toggleLike(tripId, userId));
        assertEquals("Trip not found", ex.getMessage());
    }

    @Test
    void testToggleLikeUserNotFound() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripLikesId likeId = new TripLikesId(tripId, userId);

        when(tripLikesRepository.existsById(likeId)).thenReturn(false);
        Travel travel = new Travel();
        travel.setId(tripId);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(travel));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> tripLikesService.toggleLike(tripId, userId));
        assertEquals("User not found", ex.getMessage());
    }
}