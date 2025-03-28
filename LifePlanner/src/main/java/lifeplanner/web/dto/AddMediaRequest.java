package lifeplanner.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.media.model.MediaRating;
import lifeplanner.media.model.MediaStatus;
import lifeplanner.media.model.TypeMedia;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddMediaRequest {
    @NotNull(message = "You must select status!")
    private MediaStatus status;

    @NotNull(message = "You must select type!")
    private TypeMedia type;

    @NotNull
    @Size(min = 2, max = 50, message = "Title length must be between 2 and 50 characters!")
    private String title;

    private MediaRating rating;

    private LocalDate dateRated;

    @Size(max = 40)
    private String genre;

}