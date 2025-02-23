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
public class BookLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String author;

    private String genre;

    private LocalDate dateRead;

    private Double rating;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
