package lifeplanner.recipes.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecipeLikes {

    @EmbeddedId
    private RecipeLikesId id;

    @MapsId("recipeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}