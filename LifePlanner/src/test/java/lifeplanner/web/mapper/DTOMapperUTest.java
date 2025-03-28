package lifeplanner.web.mapper;

import lifeplanner.books.model.Book;
import lifeplanner.books.model.BookRating;
import lifeplanner.books.model.BookStatus;
import lifeplanner.goals.model.Goal;
import lifeplanner.goals.model.GoalCategory;
import lifeplanner.goals.model.GoalPriority;
import lifeplanner.goals.model.GoalStatus;
import lifeplanner.media.model.Media;
import lifeplanner.media.model.MediaRating;
import lifeplanner.media.model.MediaStatus;
import lifeplanner.media.model.TypeMedia;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeCategory;
import lifeplanner.recipes.model.RecipeDifficulty;
import lifeplanner.recipes.model.RecipeIngredient;
import lifeplanner.travel.model.TransportationType;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.model.TripStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DTOMapperUTest {
    @Test
    void mapUserToUserEditRequest() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .profilePicture("profile.jpg")
                .build();

        UserEditRequest result = DTOMapper.mapUserToUserEditRequest(user);

        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getProfilePicture(), result.getProfilePicture());
    }

    @Test
    void mapBookToEditBookRequest() {
        Book book = Book.builder()
                .title("The Book")
                .author("Author Name")
                .genre("Fiction")
                .dateRead(LocalDate.now())
                .bookRating(BookRating.FIVE)
                .bookStatus(BookStatus.READ)
                .build();

        EditBookRequest result = DTOMapper.mapBookToEditBookRequest(book);

        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(book.getAuthor(), result.getAuthor());
        assertEquals(book.getGenre(), result.getGenre());
        assertEquals(book.getDateRead(), result.getDateRead());
        assertEquals(book.getBookRating(), result.getBookRating());
        assertEquals(book.getBookStatus(), result.getBookStatus());
    }

    @Test
    void mapMediaToEditMediaRequest() {
        Media media = Media.builder()
                .status(MediaStatus.WATCHED)
                .type(TypeMedia.MOVIE)
                .title("Movie Title")
                .rating(MediaRating.FOUR)
                .dateRated(LocalDate.now())
                .genre("Action")
                .build();

        EditMediaRequest result = DTOMapper.mapMediaToEditMediaRequest(media);

        assertEquals(media.getStatus(), result.getStatus());
        assertEquals(media.getType(), result.getType());
        assertEquals(media.getTitle(), result.getTitle());
        assertEquals(media.getRating(), result.getRating());
        assertEquals(media.getDateRated(), result.getDateRated());
        assertEquals(media.getGenre(), result.getGenre());
    }

    @Test
    void mapRecipeToEditRecipeRequest_withIngredients() {
        // First create a Recipe instance (parent entity)
        Recipe recipe = Recipe.builder()
                .name("Pancakes")
                .category(RecipeCategory.BREAKFAST)
                .difficulty(RecipeDifficulty.EASY)
                .cuisine("American")
                .cookingTime(30)
                .instructions("Mix and cook")
                .build();

        // Create ingredients with proper parent reference
        List<RecipeIngredient> ingredients = Arrays.asList(
                RecipeIngredient.builder()
                        .ingredientName("Flour")
                        .quantity(200.0)
                        .unit("g")
                        .recipe(recipe)  // set parent reference
                        .build(),
                RecipeIngredient.builder()
                        .ingredientName("Sugar")
                        .quantity(100.0)
                        .unit("g")
                        .recipe(recipe)  // set parent reference
                        .build()
        );

        // Set ingredients back to recipe
        recipe.setIngredients(ingredients);

        EditRecipeRequest result = DTOMapper.mapRecipeToEditRecipeRequest(recipe);

        assertEquals(recipe.getName(), result.getName());
        assertEquals(recipe.getCategory(), result.getCategory());
        assertEquals(recipe.getDifficulty(), result.getDifficulty());
        assertEquals(recipe.getCuisine(), result.getCuisine());
        assertEquals(recipe.getCookingTime(), result.getCookingTime());
        assertEquals(recipe.getInstructions(), result.getInstructions());

        assertEquals(2, result.getIngredient().size());
        assertEquals("Flour", result.getIngredient().get(0));
        assertEquals("Sugar", result.getIngredient().get(1));

        assertEquals(2, result.getQuantity().size());
        assertEquals(200.0, result.getQuantity().get(0));
        assertEquals(100.0, result.getQuantity().get(1));

        assertEquals(2, result.getUnit().size());
        assertEquals("g", result.getUnit().get(0));
        assertEquals("g", result.getUnit().get(1));
    }

    @Test
    void mapRecipeToEditRecipeRequest_withNullIngredients() {
        Recipe recipe = Recipe.builder()
                .name("Pancakes")
                .category(RecipeCategory.BREAKFAST)
                .difficulty(RecipeDifficulty.EASY)
                .cuisine("American")
                .cookingTime(30)
                .ingredients(null)
                .instructions("Mix and cook")
                .build();

        EditRecipeRequest result = DTOMapper.mapRecipeToEditRecipeRequest(recipe);

        assertEquals(recipe.getName(), result.getName());
        assertTrue(result.getIngredient().isEmpty());
        assertTrue(result.getQuantity().isEmpty());
        assertTrue(result.getUnit().isEmpty());
    }

    @Test
    void mapRecipeToEditRecipeRequest_withEmptyIngredients() {
        Recipe recipe = Recipe.builder()
                .name("Pancakes")
                .category(RecipeCategory.BREAKFAST)
                .difficulty(RecipeDifficulty.EASY)
                .cuisine("American")
                .cookingTime(30)
                .ingredients(Collections.emptyList())
                .instructions("Mix and cook")
                .build();

        EditRecipeRequest result = DTOMapper.mapRecipeToEditRecipeRequest(recipe);

        assertEquals(recipe.getName(), result.getName());
        assertTrue(result.getIngredient().isEmpty());
        assertTrue(result.getQuantity().isEmpty());
        assertTrue(result.getUnit().isEmpty());
    }

    @Test
    void mapBookToEditTravelRequest() {
        Travel trip = Travel.builder()
                .tripStatus(TripStatus.UPCOMING)
                .tripName("Summer Vacation")
                .destination("Paris")
                .startDate(LocalDate.of(2023, 6, 1))
                .endDate(LocalDate.of(2023, 6, 15))
                .accommodation("Hotel")
                .transportation(TransportationType.AIRPLANE)
                .notes("Bring camera")
                .build();

        EditTripRequest result = DTOMapper.mapBookToEditTravelRequest(trip);

        assertEquals(trip.getTripStatus(), result.getTripStatus());
        assertEquals(trip.getTripName(), result.getTripName());
        assertEquals(trip.getDestination(), result.getDestination());
        assertEquals(trip.getStartDate(), result.getStartDate());
        assertEquals(trip.getEndDate(), result.getEndDate());
        assertEquals(trip.getAccommodation(), result.getAccommodation());
        assertEquals(trip.getTransportation(), result.getTransportationType());
        assertEquals(trip.getNotes(), result.getNotes());
    }

    @Test
    void mapBookToEditGoalRequest() {
        Goal goal = Goal.builder()
                .goalName("Learn French")
                .category(GoalCategory.CAREER_AND_EDUCATION)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .priority(GoalPriority.LOW)
                .progress(25)
                .status(GoalStatus.IN_PROGRESS)
                .notes("Practice daily")
                .build();

        EditGoalRequest result = DTOMapper.mapBookToEditGoalRequest(goal);

        assertEquals(goal.getGoalName(), result.getGoalName());
        assertEquals(goal.getCategory(), result.getCategory());
        assertEquals(goal.getStartDate(), result.getStartDate());
        assertEquals(goal.getEndDate(), result.getEndDate());
        assertEquals(goal.getPriority(), result.getPriority());
        assertEquals(goal.getProgress(), result.getProgress());
        assertEquals(goal.getStatus(), result.getStatus());
        assertEquals(goal.getNotes(), result.getNotes());
    }
}