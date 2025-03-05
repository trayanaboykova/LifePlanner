package lifeplanner.travel.service;

import lifeplanner.travel.model.Travel;
import lifeplanner.travel.model.TripLikes;
import lifeplanner.travel.model.TripLikesId;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.travel.repository.TripLikesRepository;
import lifeplanner.user.model.User;
import lifeplanner.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripLikesService {

    private final TripLikesRepository tripLikesRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    @Autowired
    public TripLikesService(TripLikesRepository tripLikesRepository, TravelRepository travelRepository, UserRepository userRepository) {
        this.tripLikesRepository = tripLikesRepository;
        this.travelRepository = travelRepository;
        this.userRepository = userRepository;
    }


    public long getLikeCount(UUID tripId) {
        return tripLikesRepository.countByTripId(tripId);
    }

    public boolean toggleLike(UUID tripId, UUID userId) {
        TripLikesId likeId = new TripLikesId(tripId, userId);

        // If already liked, remove the like
        if (tripLikesRepository.existsById(likeId)) {
            tripLikesRepository.deleteById(likeId);
            return false; // false means "now unliked"
        } else {
            // Otherwise, create a new like
            Travel trip = travelRepository.findById(tripId)
                    .orElseThrow(() -> new RuntimeException("Trip not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TripLikes newLike = new TripLikes();
            newLike.setId(likeId);
            newLike.setTrip(trip);
            newLike.setUser(user);
            tripLikesRepository.save(newLike);
            return true; // true means "now liked"
        }
    }
}
