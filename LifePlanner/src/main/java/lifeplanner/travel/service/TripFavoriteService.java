package lifeplanner.travel.service;

import lifeplanner.exception.DomainException;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.model.TripFavorite;
import lifeplanner.travel.model.TripFavoriteId;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.travel.repository.TripFavoriteRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TripFavoriteService {

    private final TripFavoriteRepository tripFavoriteRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    @Autowired
    public TripFavoriteService(TripFavoriteRepository tripFavoriteRepository, TravelRepository travelRepository, UserRepository userRepository) {
        this.tripFavoriteRepository = tripFavoriteRepository;
        this.travelRepository = travelRepository;
        this.userRepository = userRepository;
    }


    public boolean toggleFavoriteTrip(UUID tripId, UUID userId) {
        TripFavoriteId favoriteId = new TripFavoriteId(tripId, userId);
        if (tripFavoriteRepository.existsById(favoriteId)) {
            tripFavoriteRepository.deleteById(favoriteId);
            return false;
        } else {
            Travel travel = travelRepository.findById(tripId)
                    .orElseThrow(() -> new DomainException("Trip not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DomainException("User not found"));
            TripFavorite tripFavorite = new TripFavorite();
            tripFavorite.setId(favoriteId);
            tripFavorite.setTrip(travel);
            tripFavorite.setUser(user);
            tripFavoriteRepository.save(tripFavorite);
            return true;
        }
    }

    public long getFavoriteCount(UUID tripId) {
        return tripFavoriteRepository.countByTripId(tripId);
    }

    public List<Travel> getFavoritesByUser(User user) {
        List<TripFavorite> favorites = tripFavoriteRepository.findAllByUser(user);
        return favorites.stream()
                .map(TripFavorite::getTrip)
                .collect(Collectors.toList());
    }

    public void removeFavorite(User user, UUID tripId) {
        Optional<TripFavorite> favoriteOpt = tripFavoriteRepository.findByUserAndTripId(user, tripId);
        favoriteOpt.ifPresent(tripFavoriteRepository::delete);
    }
}