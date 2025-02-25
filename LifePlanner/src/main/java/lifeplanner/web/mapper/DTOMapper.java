package lifeplanner.web.mapper;

import lifeplanner.books.model.Book;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.EditBookRequest;
import lifeplanner.web.dto.UserEditRequest;
import lombok.experimental.UtilityClass;

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
}
