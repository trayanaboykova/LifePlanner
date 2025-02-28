package lifeplanner.web.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.recipes.model.RecipeCategory;
import lifeplanner.recipes.model.RecipeDifficulty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EditRecipeRequest {

    @NotNull(message = "You must select a name!")
    @Size(min = 2, max = 50, message = "Name length must be between 2 and 50 characters!")
    private String name;

    private RecipeCategory category;

    private RecipeDifficulty difficulty;

    private String cuisine;

    private Integer cookingTime;

    private List<String> ingredient;

    private List<Double> quantity;

    private List<String> unit;

    @Lob
    private String instructions;

}
