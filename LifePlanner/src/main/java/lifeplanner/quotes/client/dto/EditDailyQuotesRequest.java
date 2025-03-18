package lifeplanner.quotes.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditDailyQuotesRequest {
    private UUID id;
    @NotBlank(message = "Image URL cannot be blank")
    private String quoteImage;
    private UUID userId;
}
