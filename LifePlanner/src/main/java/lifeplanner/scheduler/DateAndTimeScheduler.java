package lifeplanner.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DateAndTimeScheduler {
    private LocalDateTime currentDateTime;

    @Scheduled(cron = "0 * * * * ?")
    public void updateCurrentDateTime() {
        currentDateTime = LocalDateTime.now();
        log.info("Current Date and Time updated: {}", currentDateTime);
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime != null ? currentDateTime : LocalDateTime.now();
    }
}