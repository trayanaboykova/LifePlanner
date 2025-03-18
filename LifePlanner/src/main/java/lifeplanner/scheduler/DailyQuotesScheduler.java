package lifeplanner.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class DailyQuotesScheduler {
    // List of daily quote image URLs (replace with your actual image URLs)
    private final List<String> quoteImageUrls = List.of(
            "https://i.pinimg.com/736x/a2/ee/8f/a2ee8f605b6b01578c057a3c6485ef53.jpg",
            "https://i.pinimg.com/736x/96/c5/a6/96c5a6095b0f7b38f77b2413b49313c0.jpg",
            "https://i.pinimg.com/736x/50/13/a4/5013a4b15d5d0eaee10c8076891dbf78.jpg",
            "https://i.pinimg.com/736x/42/a5/38/42a5383dee2affc7bf6ac6bef1c78d55.jpg",
            "https://i.pinimg.com/736x/4d/e5/fa/4de5fa3b31a61592af4bd01ed62d62f6.jpg",
            "https://i.pinimg.com/736x/0d/9f/0a/0d9f0a6e8b0c89cf3de9fd488e8a1ae7.jpg",
            "https://i.pinimg.com/736x/05/95/ea/0595ea45cc0513cb60f48abaa6663264.jpg",
            "https://i.pinimg.com/736x/e4/03/50/e403508892165b7091d28ac20d522bf3.jpg",
            "https://i.pinimg.com/736x/15/1c/3e/151c3e0ca541e1625324c2714a1283ed.jpg",
            "https://i.pinimg.com/736x/3e/b0/ad/3eb0add18f8f4c98634a7619137ae69e.jpg",
            "https://i.pinimg.com/736x/b7/04/a7/b704a7e8937278763fd84b97638f7209.jpg",
            "https://i.pinimg.com/736x/05/9b/1f/059b1f1f96a9425b5825256c02730f8a.jpg",
            "https://i.pinimg.com/736x/06/33/60/063360014b10a06475bde73192d6d448.jpg",
            "https://i.pinimg.com/736x/8f/f5/92/8ff592a0ceadfbb9eee2b5a8ad806240.jpg",
            "https://i.pinimg.com/736x/fa/ae/ef/faaeefb63451c4ba36ae45782c64f696.jpg",
            "https://i.pinimg.com/736x/16/ca/a8/16caa8dee7cfb0ae842c05c211f338b5.jpg",
            "https://i.pinimg.com/736x/2e/f4/4c/2ef44c6e45b8aabedc884aa16179f08b.jpg",
            "https://i.pinimg.com/736x/6d/10/6f/6d106fc88cd5113b3c783508eba04000.jpg",
            "https://i.pinimg.com/736x/dc/de/ea/dcdeeaed9f8d4f1f817a3e739d03eac4.jpg",
            "https://i.pinimg.com/736x/8b/00/c5/8b00c55a20ab81e4a0fefaf87d785c8d.jpg",
            "https://i.pinimg.com/736x/9a/d8/89/9ad889e04afa23152bf86613a8f976b6.jpg"
    );

    // This field will hold the current daily quote image URL
    private String currentQuoteUrl;

    @Scheduled(cron = "* 30 * * * *")
    public void updateDailyQuote() {
        Random random = new Random();
        currentQuoteUrl = quoteImageUrls.get(random.nextInt(quoteImageUrls.size()));
        log.info("Daily quote image updated: {}", currentQuoteUrl);
    }

    public String getCurrentQuoteUrl() {
        // Return a default if not yet updated
        return currentQuoteUrl != null ? currentQuoteUrl : "https://i.pinimg.com/736x/04/a9/0c/04a90c5acb774d0413205cb5cac1fd6f.jpg";
    }
}