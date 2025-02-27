package lifeplanner.books.model;

import jakarta.persistence.*;
import lifeplanner.user.model.User;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String genre;

    private LocalDate dateRead;

    @Enumerated(EnumType.STRING)
    private BookRating bookRating;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private boolean visible;
}