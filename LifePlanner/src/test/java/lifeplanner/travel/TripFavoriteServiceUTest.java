package lifeplanner.travel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lifeplanner.exception.DomainException;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.model.TripFavorite;
import lifeplanner.travel.model.TripFavoriteId;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.travel.repository.TripFavoriteRepository;
import lifeplanner.travel.service.TripFavoriteService;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TripFavoriteServiceUTest {

    @Mock
    private TripFavoriteRepository tripFavoriteRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripFavoriteService tripFavoriteService;

    @Test
    void testToggleFavoriteTripRemove() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripFavoriteId favoriteId = new TripFavoriteId(tripId, userId);

        when(tripFavoriteRepository.existsById(favoriteId)).thenReturn(true);

        boolean result = tripFavoriteService.toggleFavoriteTrip(tripId, userId);

        assertFalse(result);
        verify(tripFavoriteRepository).deleteById(favoriteId);
    }

    @Test
    void testToggleFavoriteTripAdd() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripFavoriteId favoriteId = new TripFavoriteId(tripId, userId);

        when(tripFavoriteRepository.existsById(favoriteId)).thenReturn(false);

        Travel travel = new Travel();
        travel.setId(tripId);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(travel));

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = tripFavoriteService.toggleFavoriteTrip(tripId, userId);

        assertTrue(result);
        ArgumentCaptor<TripFavorite> captor = ArgumentCaptor.forClass(TripFavorite.class);
        verify(tripFavoriteRepository).save(captor.capture());
        TripFavorite savedFavorite = captor.getValue();
        assertEquals(favoriteId, savedFavorite.getId());
        assertEquals(travel, savedFavorite.getTrip());
        assertEquals(user, savedFavorite.getUser());
    }

    @Test
    void testToggleFavoriteTripTripNotFound() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripFavoriteId favoriteId = new TripFavoriteId(tripId, userId);
        when(tripFavoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(travelRepository.findById(tripId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> tripFavoriteService.toggleFavoriteTrip(tripId, userId));
        assertEquals("Trip not found", ex.getMessage());
    }

    @Test
    void testToggleFavoriteTripUserNotFound() {
        UUID tripId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TripFavoriteId favoriteId = new TripFavoriteId(tripId, userId);
        when(tripFavoriteRepository.existsById(favoriteId)).thenReturn(false);

        Travel travel = new Travel();
        travel.setId(tripId);
        when(travelRepository.findById(tripId)).thenReturn(Optional.of(travel));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> tripFavoriteService.toggleFavoriteTrip(tripId, userId));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testGetFavoriteCount() {
        UUID tripId = UUID.randomUUID();
        when(tripFavoriteRepository.countByTripId(tripId)).thenReturn(5L);
        long count = tripFavoriteService.getFavoriteCount(tripId);
        assertEquals(5L, count);
    }

    @Test
    void testGetFavoritesByUser() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Travel travel1 = new Travel();
        travel1.setId(UUID.randomUUID());
        Travel travel2 = new Travel();
        travel2.setId(UUID.randomUUID());

        TripFavorite fav1 = new TripFavorite();
        fav1.setTrip(travel1);
        TripFavorite fav2 = new TripFavorite();
        fav2.setTrip(travel2);

        when(tripFavoriteRepository.findAllByUser(user)).thenReturn(List.of(fav1, fav2));

        List<Travel> favorites = tripFavoriteService.getFavoritesByUser(user);
        assertEquals(2, favorites.size());
        assertTrue(favorites.contains(travel1));
        assertTrue(favorites.contains(travel2));
    }

    @Test
    void testRemoveFavoritePresent() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID tripId = UUID.randomUUID();

        TripFavorite favorite = new TripFavorite();
        when(tripFavoriteRepository.findByUserAndTripId(user, tripId))
                .thenReturn(Optional.of(favorite));

        tripFavoriteService.removeFavorite(user, tripId);

        verify(tripFavoriteRepository).delete(favorite);
    }

    @Test
    void testRemoveFavoriteNotPresent() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID tripId = UUID.randomUUID();

        when(tripFavoriteRepository.findByUserAndTripId(user, tripId))
                .thenReturn(Optional.empty());

        // Should not throw exception, simply do nothing.
        tripFavoriteService.removeFavorite(user, tripId);
        verify(tripFavoriteRepository, never()).delete(any());
    }
}