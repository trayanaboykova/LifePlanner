package lifeplanner.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.books.model.BookRating;
import lifeplanner.books.model.BookStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class EditBookRequest {

    @NotNull(message = "You must select status!")
    private BookStatus bookStatus;

    @NotNull
    @Size(min = 2, max = 50, message = "Title length must be between 2 and 50 characters!")
    private String title;

    @NotNull
    @Size(min = 2, max = 50, message = "Author length must be between 2 and 50 characters!")
    private String author;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateRead;

    private BookRating bookRating;

    @Size(max = 40)
    private String genre;

}