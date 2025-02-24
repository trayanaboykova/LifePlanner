package lifeplanner.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lifeplanner.books.model.BookRating;
import lifeplanner.books.model.BookStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddBookRequest {

    @NotNull(message = "You must select status!")
    private BookStatus bookStatus;

    @NotNull
    @Size(min = 2, max = 50, message = "Title length must be between 2 and 50 characters!")
    private String title;

    @NotNull
    @Size(min = 2, max = 50, message = "Author length must be between 2 and 50 characters!")
    private String author;

    private LocalDate dateRead;

    private BookRating bookRating;

    @Size(max = 40)
    private String genre;

}
