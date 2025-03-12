package lifeplanner.web.mapper;

import lifeplanner.books.model.Book;
import lifeplanner.goals.model.Goal;
import lifeplanner.media.model.Media;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.recipes.model.RecipeIngredient;
import lifeplanner.travel.model.Travel;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.*;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DTOMapper {

    public static UserEditRequest mapUserToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    public static EditBookRequest mapBookToEditBookRequest(Book book) {
        return EditBookRequest.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .dateRead(book.getDateRead())
                .bookRating(book.getBookRating())
                .bookStatus(book.getBookStatus())
                .build();
    }

    public static EditMediaRequest mapMediaToEditMediaRequest(Media media) {
        return EditMediaRequest.builder()
                .status(media.getStatus())
                .type(media.getType())
                .title(media.getTitle())
                .rating(media.getRating())
                .dateRated(media.getDateRated())
                .genre(media.getGenre())
                .build();
    }

    public static EditRecipeRequest mapRecipeToEditRecipeRequest(Recipe recipe) {
        List<String> ingredientNames = recipe.getIngredients() == null
                ? new ArrayList<>()
                : recipe.getIngredients().stream()
                .map(RecipeIngredient::getIngredientName)
                .collect(Collectors.toList());

        List<Double> quantities = recipe.getIngredients() == null
                ? new ArrayList<>()
                : recipe.getIngredients().stream()
                .map(RecipeIngredient::getQuantity)
                .collect(Collectors.toList());

        List<String> units = recipe.getIngredients() == null
                ? new ArrayList<>()
                : recipe.getIngredients().stream()
                .map(RecipeIngredient::getUnit)
                .collect(Collectors.toList());

        return EditRecipeRequest.builder()
                .name(recipe.getName())
                .category(recipe.getCategory())
                .difficulty(recipe.getDifficulty())
                .cuisine(recipe.getCuisine())
                .cookingTime(recipe.getCookingTime())
                .ingredient(ingredientNames)
                .quantity(quantities)
                .unit(units)
                .instructions(recipe.getInstructions())
                .build();

    }

    public static EditTripRequest mapBookToEditTravelRequest(Travel trip) {
        return EditTripRequest.builder()
                .tripStatus(trip.getTripStatus())
                .tripName(trip.getTripName())
                .destination(trip.getDestination())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .accommodation(trip.getAccommodation())
                .transportationType(trip.getTransportation())
                .notes(trip.getNotes())
                .build();
    }

    public static EditGoalRequest mapBookToEditGoalRequest(Goal goal) {
        return EditGoalRequest.builder()
                .goalName(goal.getGoalName())
                .category(goal.getCategory())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .priority(goal.getPriority())
                .progress(goal.getProgress())
                .status(goal.getStatus())
                .notes(goal.getNotes())
                .build();
    }
}