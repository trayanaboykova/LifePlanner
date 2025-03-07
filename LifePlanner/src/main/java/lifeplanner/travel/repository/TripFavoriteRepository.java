package lifeplanner.travel.repository;

import lifeplanner.travel.model.TripFavorite;
import lifeplanner.travel.model.TripFavoriteId;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripFavoriteRepository extends JpaRepository<TripFavorite, TripFavoriteId> {

    long countByTripId(UUID tripId);

    boolean existsById(TripFavoriteId id);

    List<TripFavorite> findAllByUser(User user);

    Optional<TripFavorite> findByUserAndTripId(User user, UUID tripId);
}