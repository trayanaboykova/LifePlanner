package lifeplanner.user.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SharedPosts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(nullable = false)
    private UUID contentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Like counter field to hold the total number of likes
    @Builder.Default
    private int likeCount = 0;

    // Favorites counter field to hold the total number of favorites
    @Builder.Default
    private int favoriteCount = 0;
}
