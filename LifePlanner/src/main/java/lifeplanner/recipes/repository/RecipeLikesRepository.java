package lifeplanner.recipes.repository;

import lifeplanner.books.model.BookLikesId;
import lifeplanner.recipes.model.RecipeLikes;
import lifeplanner.recipes.model.RecipeLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipeLikesRepository extends JpaRepository<RecipeLikes, RecipeLikesId> {

    long countByRecipeId(UUID recipeId);

    boolean existsById(RecipeLikesId id);

}
