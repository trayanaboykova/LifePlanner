package lifeplanner.travel.repository;

import lifeplanner.travel.model.TripLikes;
import lifeplanner.travel.model.TripLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripLikesRepository extends JpaRepository<TripLikes, TripLikesId> {
    // Count how many likes a trip has
    long countByTripId(UUID tripId);

    // Check if a user has already liked a trip
    boolean existsById(TripLikesId id);
}
