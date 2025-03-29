package lifeplanner.travel.repository;

import lifeplanner.travel.model.TripLikes;
import lifeplanner.travel.model.TripLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripLikesRepository extends JpaRepository<TripLikes, TripLikesId> {

    long countByTripId(UUID tripId);

    boolean existsById(TripLikesId id);

}