package lifeplanner.recipes.repository;

import lifeplanner.recipes.model.RecipeFavorite;
import lifeplanner.recipes.model.RecipeFavoriteId;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeFavoriteRepository extends JpaRepository<RecipeFavorite, RecipeFavoriteId> {

    long countByRecipeId(UUID recipeId);

    boolean existsById(RecipeFavoriteId id);

    List<RecipeFavorite> findAllByUser(User user);

    Optional<RecipeFavorite> findByUserAndRecipeId(User user, UUID recipeId);

}