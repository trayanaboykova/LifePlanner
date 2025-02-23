package lifeplanner.user.model;


import jakarta.persistence.*;
import lifeplanner.books.model.BookLibrary;
import lifeplanner.goals.model.Goal;
import lifeplanner.media.model.Media;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.travel.model.Travel;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePicture;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<BookLibrary> books;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Media> media;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Recipe> recipes;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Travel> travel;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Goal> goals;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<SharedPosts> sharedPosts;
}
