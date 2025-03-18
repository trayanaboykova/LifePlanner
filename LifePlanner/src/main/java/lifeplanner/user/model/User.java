package lifeplanner.user.model;


import jakarta.persistence.*;
import lifeplanner.books.model.Book;
import lifeplanner.goals.model.Goal;
import lifeplanner.media.model.Media;
import lifeplanner.recipes.model.Recipe;
import lifeplanner.travel.model.Travel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Media> media = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Travel> travel = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<SharedPosts> sharedPosts = new ArrayList<>();

    private String currentDailyQuoteImageUrl;
}