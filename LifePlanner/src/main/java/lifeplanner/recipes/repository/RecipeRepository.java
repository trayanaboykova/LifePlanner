package lifeplanner.recipes.repository;

import lifeplanner.recipes.model.Recipe;
import lifeplanner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    List<Recipe> findAllByOwner(User owner);

    List<Recipe> findAllByVisibleTrue();
}
